package com.sip.ams.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sip.ams.entities.Article;
import com.sip.ams.entities.Provider;
import com.sip.ams.exceptions.ResourceNotFoundException;
import com.sip.ams.repositories.ArticleRepository;
import com.sip.ams.repositories.ProviderRepository;
import com.sip.ams.services.ArticleService;

import jakarta.validation.Valid;
@RestController
@RequestMapping({"/articles"})
@CrossOrigin("*")
public class ArticleRestController {
	@Autowired
	private ArticleService articleService;
	/*@Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ProviderRepository providerRepository;*/

    @GetMapping
    public List<Article> getAllArticles() {
        return (List<Article>) articleService.findAll();
    }

//    @PostMapping("{providerId}")
//    public Article create(@PathVariable (value = "providerId") Long providerId, @Valid @RequestBody Article article) {
//    	return articleService.create(providerId, article);
//			}
    @PostMapping("/{providerId}")
	Article createArticle(@PathVariable(value = "providerId") Long providerId, 
			@RequestParam("label") String label,
			@RequestParam("price") float price, 
			@RequestParam(name="imageFace") MultipartFile fileFace,
			@RequestParam(name="imageProfile") MultipartFile fileProfile) 
			throws IOException {
		return articleService.create(providerId, label,price,fileFace,fileProfile);
	}
    
    
    
    @DeleteMapping("/{articleId}")
    public Article deleteArticle(@PathVariable (value = "articleId") Long articleId) {
        return articleService.deleteArticle( articleId);
    }
    
    @GetMapping("/{articleId}")
	public Article findOneProvider(@PathVariable Long articleId) {
		return  articleService.findOneArticle(articleId);
	}
    
//    @PutMapping("/{providerId}/{articleId}")
//    public Article updateArticle(@PathVariable(value = "providerId")  Long providerId,
//                                 @PathVariable(value = "articleId")  Long articleId,
//                                 @Valid @RequestBody Article articleRequest) {
//    	return articleService.updateArticle(providerId, articleId, articleRequest);
//    }
    
    @PutMapping("/{providerId}/{articleId}")
	public Article updateArticle(@PathVariable(value = "providerId") Long providerId,
			@PathVariable(value = "articleId") Long articleId, 
			@RequestParam("label") String label,
			@RequestParam("price") float price, 
			@RequestParam(name="imageFace",required = false) MultipartFile fileFace,
			@RequestParam(name="imageProfile",required = false) MultipartFile fileProfile) {
		return articleService.updateArticle(providerId, articleId, label,price,fileFace,fileProfile);
	}
    
//        if(!providerRepository.existsById(providerId)) {
//            throw new ResourceNotFoundException("ProviderId " + providerId + " not found");
//        }
//
//        return articleRepository.findById(articleId).map(article -> {
//        	 article.setPrice(articleRequest.getPrice());
//             article.setLabel(articleRequest.getLabel()); 
//             article.setLabel(articleRequest.getPicture()); 
//        return articleRepository.save(article);
//        }).orElseThrow(() -> new ResourceNotFoundException("ArticleId " + articleId + "not found"));
//    }
//    
//    @DeleteMapping("/delete/{articleId}")
//    public ResponseEntity<?> deleteArticle(@PathVariable (value = "articleId") Long articleId) {
//        return articleRepository.findById(articleId).map(article -> {
//            articleRepository.delete(article);
//            return ResponseEntity.ok().build();
//        }).orElseThrow(() -> new ResourceNotFoundException("Article not found with id " + articleId));
//    }

}
