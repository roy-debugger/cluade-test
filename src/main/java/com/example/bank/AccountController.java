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
        List<Account> otherAccounts = accountService.getOtherAccounts(id); // 본인 계좌 제외한 계좌 목록
        
        // 디버깅 로그 추가
        System.out.println("계좌 상세 조회 - 계좌 ID: " + id);
        System.out.println("전체 계좌 수: " + accountService.getAllAccounts().size());
        System.out.println("다른 계좌 수: " + otherAccounts.size());
        otherAccounts.forEach(acc -> System.out.println("  - 계좌번호: " + acc.getId() + ", 예금주: " + acc.getAccountHolder()));
        
        model.addAttribute("account", account);
        model.addAttribute("transactions", transactions);
        model.addAttribute("otherAccounts", otherAccounts); // 다른 계좌 목록 추가
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
                          @RequestParam Long toAccountId,
                          @RequestParam BigDecimal amount,
                          @RequestParam(required = false) String description,
                          RedirectAttributes redirectAttributes) {
        try {
            accountService.transfer(id, toAccountId, amount, description);
            redirectAttributes.addFlashAttribute("successMessage", 
                "송금이 완료되었습니다. 수취인: 계좌번호 " + toAccountId + "번, 금액: ₩" + amount);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/accounts/" + id;
    }
}