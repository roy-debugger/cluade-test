package com.example.bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    @GetMapping("/")
    public String index() {
        return "redirect:/accounts";
    }
    
    @GetMapping("/accounts")
    public String listAccounts(Model model) {
        List<Account> accounts = accountService.getAllAccounts();
        model.addAttribute("accounts", accounts);
        return "accounts";
    }
    
    @GetMapping("/accounts/new")
    public String newAccountForm() {
        return "account-form";
    }
    
    @PostMapping("/accounts")
    public String createAccount(@RequestParam String accountHolder, 
                               @RequestParam(required = false) BigDecimal initialDeposit,
                               RedirectAttributes redirectAttributes) {
        try {
            Account account = accountService.createAccount(accountHolder, initialDeposit);
            redirectAttributes.addFlashAttribute("successMessage", 
                "계좌가 성공적으로 개설되었습니다. 계좌번호: " + account.getId());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/accounts/new";
        }
        return "redirect:/accounts";
    }
    
    @GetMapping("/accounts/{id}")
    public String accountDetail(@PathVariable Long id, Model model) {
        Optional<Account> accountOpt = accountService.getAccount(id);
        if (accountOpt.isEmpty()) {
            return "redirect:/accounts";
        }
        
        Account account = accountOpt.get();
        List<Transaction> transactions = accountService.getTransactionHistory(id);
        
        // 송금 대상 계좌 목록 (본인 계좌 제외)
        List<Account> availableAccounts = accountService.getAllAccounts()
            .stream()
            .filter(acc -> !acc.getId().equals(id))
            .toList();
        
        model.addAttribute("account", account);
        model.addAttribute("transactions", transactions);
        model.addAttribute("availableAccounts", availableAccounts);
        return "account-detail";
    }
    
    @PostMapping("/accounts/{id}/deposit")
    public String deposit(@PathVariable Long id,
                         @RequestParam BigDecimal amount,
                         @RequestParam(required = false) String description,
                         RedirectAttributes redirectAttributes) {
        try {
            accountService.deposit(id, amount, description);
            redirectAttributes.addFlashAttribute("successMessage", 
                "입금이 완료되었습니다. 금액: ₩" + amount);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/accounts/" + id;
    }
    
    @PostMapping("/accounts/{id}/withdraw")
    public String withdraw(@PathVariable Long id,
                          @RequestParam BigDecimal amount,
                          @RequestParam(required = false) String description,
                          RedirectAttributes redirectAttributes) {
        try {
            accountService.withdraw(id, amount, description);
            redirectAttributes.addFlashAttribute("successMessage", 
                "출금이 완료되었습니다. 금액: ₩" + amount);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/accounts/" + id;
    }
    
    @PostMapping("/accounts/{id}/transfer")
    public String transfer(@PathVariable Long id,
                         @RequestParam Long targetAccountId,
                         @RequestParam BigDecimal amount,
                         @RequestParam(required = false) String description,
                         RedirectAttributes redirectAttributes) {
        try {
            accountService.transfer(id, targetAccountId, amount, description);
            redirectAttributes.addFlashAttribute("successMessage", 
                "송금이 완료되었습니다. 수취인: 계좌번호 " + targetAccountId + "번");
        } catch (IllegalArgumentException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("not found")) {
                if (errorMessage.contains("Recipient")) {
                    errorMessage = "존재하지 않는 계좌번호입니다.";
                } else if (errorMessage.contains("Sender")) {
                    errorMessage = "송금인 계좌를 찾을 수 없습니다.";
                }
            } else if (errorMessage.contains("Insufficient balance")) {
                errorMessage = "잔액이 부족합니다.";
            } else if (errorMessage.contains("same account")) {
                errorMessage = "본인 계좌로는 송금할 수 없습니다.";
            } else if (errorMessage.contains("positive")) {
                errorMessage = "송금 금액은 0보다 커야 합니다.";
            }
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
        }
        
        return "redirect:/accounts/" + id;
    }
}