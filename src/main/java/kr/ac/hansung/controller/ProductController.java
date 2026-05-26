package kr.ac.hansung.controller;

import jakarta.validation.Valid;
import kr.ac.hansung.dto.ProductDto;
import kr.ac.hansung.entity.Product;
import kr.ac.hansung.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String list(
        @RequestParam(required = false) String keyword,
        @PageableDefault(size = 5) Pageable pageable,
        Model model
    ) {
        String normalizedKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        Page<Product> productPage = productService.findAll(normalizedKeyword, pageable);

        model.addAttribute("productPage", productPage);
        model.addAttribute("keyword", normalizedKeyword);
        return "products/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        return "products/detail";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("productDto", new ProductDto());
        model.addAttribute("formTitle", "상품 등록");
        model.addAttribute("formDescription", "새로운 상품 정보를 입력해주세요.");
        model.addAttribute("submitText", "등록");
        model.addAttribute("formAction", "/products");
        return "products/add";
    }

    @PostMapping
    public String save(
        @Valid @ModelAttribute("productDto") ProductDto dto,
        BindingResult bindingResult,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "상품 등록");
            model.addAttribute("formDescription", "새로운 상품 정보를 입력해주세요.");
            model.addAttribute("submitText", "등록");
            model.addAttribute("formAction", "/products");
            return "products/add";
        }

        productService.save(dto);
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);

        ProductDto dto = new ProductDto();
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setDescription(product.getDescription());

        model.addAttribute("productDto", dto);
        model.addAttribute("productId", id);
        return "products/edit";
    }

    @PostMapping("/{id}/edit")
    public String editProduct(
        @PathVariable Long id,
        @Valid @ModelAttribute("productDto") ProductDto productDto,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes ra
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("productId", id);
            return "products/edit";
        }

        productService.updateProduct(id, productDto);
        ra.addFlashAttribute("successMessage", "상품이 수정되었습니다.");
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/products";
    }
}
