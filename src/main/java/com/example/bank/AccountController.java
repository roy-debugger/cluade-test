package com.example.bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    /**
     * 기본 경로를 계좌 목록 페이지로 리다이렉트한다.
     * 
     * @return 계좌 목록 페이지로의 리다이렉트 경로
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/accounts";
    }
    
    /**
     * 전체 계좌 목록을 조회한다.
     * 
     * @param model 뷰에 전달할 데이터 모델
     * @return 계좌 목록 페이지 템플릿명
     */
    @GetMapping("/accounts")
    public String listAccounts(Model model) {
        List<Account> accounts = accountService.getAllAccounts();
        model.addAttribute("accounts", accounts);
        return "accounts";
    }
    
    /**
     * 새 계좌 개설 폼 페이지를 반환한다.
     * 
     * @return 계좌 개설 폼 페이지 템플릿명
     */
    @GetMapping("/accounts/new")
    public String newAccountForm() {
        return "account-form";
    }
    
    /**
     * 새 계좌을 개설한다.
     * 
     * @param accountHolder 예금주 이름
     * @param initialDeposit 초기 예치금 (옵션)
     * @param redirectAttributes 리다이렉트 메시지 전달용
     * @return 계좌 목록 페이지로 리다이렉트 (성공) 또는 계좌 개설 폼으로 리다이렉트 (실패)
     */
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
    
    /**
     * 계좌 상세 정보를 조회하고 송금용 계좌 목록을 제공한다.
     * 
     * @param id 조회할 계좌 ID
     * @param model 뷰에 전달할 데이터 모델
     * @return 계좌 상세 페이지 템플릿명
     */
    @GetMapping("/accounts/{id}")
    public String accountDetail(@PathVariable Long id, Model model) {
        Optional<Account> accountOpt = accountService.getAccount(id);
        if (accountOpt.isEmpty()) {
            return "redirect:/accounts";
        }
        
        Account account = accountOpt.get();
        List<Transaction> transactions = accountService.getTransactionHistory(id);
        
        // 송금용 계좌 목록 (본인 계좌 제외)
        List<Account> transferableAccounts = accountService.getAllAccounts().stream()
            .filter(acc -> !acc.getId().equals(id))
            .collect(Collectors.toList());
        
        model.addAttribute("account", account);
        model.addAttribute("transactions", transactions);
        model.addAttribute("transferableAccounts", transferableAccounts);
        return "account-detail";
    }
    
    /**
     * 계좌에 입금을 처리한다.
     * 
     * @param id 계좌 ID
     * @param amount 입금액 (양수여야 함)
     * @param description 입금 설명 (옵션)
     * @param redirectAttributes 리다이렉트 메시지 전달용
     * @return 계좌 상세 페이지로 리다이렉트
     */
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
    
    /**
     * 계좌에서 출금을 처리한다.
     * 
     * @param id 계좌 ID
     * @param amount 출금액 (양수여야 하며 잔액 이하여야 함)
     * @param description 출금 설명 (옵션)
     * @param redirectAttributes 리다이렉트 메시지 전달용
     * @return 계좌 상세 페이지로 리다이렉트
     */
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
    
    /**
     * 계좌 간 송금을 처리한다.
     * 
     * @param fromId 송금하는 계좌 ID
     * @param toAccountId 송금받는 계좌 ID
     * @param amount 송금액 (양수여야 함)
     * @param description 송금 설명 (옵션)
     * @param redirectAttributes 리다이렉트 메시지 전달용
     * @return 송금한 계좌 상세 페이지로 리다이렉트
     */
    @PostMapping("/accounts/{id}/transfer")
    public String transfer(@PathVariable("id") Long fromId,
                          @RequestParam Long toAccountId,
                          @RequestParam BigDecimal amount,
                          @RequestParam(required = false) String description,
                          RedirectAttributes redirectAttributes) {
        try {
            accountService.transfer(fromId, toAccountId, amount, description);
            redirectAttributes.addFlashAttribute("successMessage", 
                "송금이 완료되었습니다. 금액: ₩" + amount);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/accounts/" + fromId;
    }
}