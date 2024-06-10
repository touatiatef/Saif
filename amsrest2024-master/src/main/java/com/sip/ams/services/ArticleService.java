package com.sip.ams.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sip.ams.entities.Article;
import com.sip.ams.entities.Provider;
import com.sip.ams.exceptions.ResourceNotFoundException;
import com.sip.ams.repositories.ArticleRepository;
import com.sip.ams.repositories.ProviderRepository;

@Service
public class ArticleService {
	private final Path root = Paths.get(System.getProperty("user.dir") + "/src/main/resources/static/uploads");
@Autowired
private ArticleRepository articleRepository ;
@Autowired
private ProviderRepository providerRepository;
	
	
	protected static String getSaltString() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 18) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;

	}
	
	public List<Article> findAll() {
		return (List<Article>) articleRepository.findAll();
	}
	
//	public Article create(Long providerId, Article article) {
//		return providerRepository.findById(providerId).map(provider -> {
//			article.setProvider(provider);
//			return articleRepository.save(article);
//		}).orElseThrow(() -> new ResourceNotFoundException("ProviderId " + providerId + " not found"));
//	}
	
	public Article create(Long providerId, String label,float price,MultipartFile fileFace,MultipartFile fileProfile) throws IOException{
		String newImageFaceName = getSaltString().concat(fileFace.getOriginalFilename());
		String newImageProfileName = getSaltString().concat(fileProfile.getOriginalFilename());
		
		
		
		return providerRepository.findById(providerId).map(provider -> {
			
			try {
				Files.copy(fileFace.getInputStream(), this.root.resolve(newImageFaceName));
				Files.copy(fileProfile.getInputStream(), this.root.resolve(newImageProfileName));
			} catch (Exception e) {
				throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
			}
			System.out.println("price="+price);
			//Article article = new Article(label, price, newImageFaceName, newImageProfileName,provider);
			
			  Article article = new Article();
			  article.setLabel(label);
			  article.setPrice(price); article.setProvider(provider);
			  article.setImageFace(newImageFaceName);
			  article.setImageProfile(newImageProfileName);
			 
			
			
			return articleRepository.save(article);
		}).orElseThrow(() -> new ResourceNotFoundException("ProviderId " + providerId + " not found"));
	
	}
	
	
//*********** first update version****************
//	public Article update(Long articleId,  Article articleRequest) {
//		return articleRepository.findById(articleId).map(article -> {
//			
//			article.setId(articleRequest.getId());
//			article.setLabel(articleRequest.getLabel());
//			article.setPrice(articleRequest.getPrice());
//			article.setPicture(articleRequest.getPicture());			
//			return articleRepository.save(article);
//		}).orElseThrow(() -> new ResourceNotFoundException("articleId " + articleId + " not found"));
//	}
	//*********** second update version****************
//	public Article updateArticle(Long providerId,Long articleId, Article articleRequest) {
//		if(!providerRepository.existsById(providerId)) {
//			throw new ResourceNotFoundException("ProviderId " + providerId + " not found");
//		}
//		
//		return providerRepository.findById(providerId).map(provider -> {
//			return articleRepository.findById(articleId).map(article -> {
//				article.setProvider(provider);
//			article.setLabel(articleRequest.getLabel());
//			article.setPrice(articleRequest.getPrice());
//			article.setPicture(articleRequest.getPicture());			
//			return articleRepository.save(article);
//			}).orElseThrow(() -> new ResourceNotFoundException("articleId " + articleId + " not found"));
//		}).orElseThrow(() -> new ResourceNotFoundException("ProviderId " + articleId + " not found"));
//	}
	
	public Article updateArticle(Long providerId, Long articleId, 
			String label,float price,MultipartFile fileFace,MultipartFile fileProfile) {
		if (!providerRepository.existsById(providerId)) {
			throw new ResourceNotFoundException("ProviderId " + providerId + " not found");
		}
		return providerRepository.findById(providerId).map(provider -> {
			return articleRepository.findById(articleId).map(article -> {
				
				if(fileFace != null)
				{
					// STEP 1 : delete Old Image from server
					String OldImageFaceName = article.getImageFace();

					////////
					try {
						File f = new File(this.root + "/" + OldImageFaceName); // file to be delete
						if (f.delete()) // returns Boolean value
						{
							System.out.println(f.getName() + " deleted"); // getting and printing the file name
						} else {
							System.out.println("failed");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				if(fileProfile != null)
				{
					// STEP 1 : delete Old Image from server
					String OldImageProfileName = article.getImageProfile();

					////////
					try {
						File f = new File(this.root + "/" + OldImageProfileName); // file to be delete
						if (f.delete()) // returns Boolean value
						{
							System.out.println(f.getName() + " deleted"); // getting and printing the file name
						} else {
							System.out.println("failed");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				/// STEP 2 : Upload new image to server
				String newImageFaceName = article.getImageFace();
				if (fileFace != null) {
					newImageFaceName = getSaltString().concat(fileFace.getOriginalFilename());
					try {
						Files.copy(fileFace.getInputStream(), this.root.resolve(newImageFaceName));
					} catch (Exception e) {
						throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
					}
				}
				
				String newImageProfileName = article.getImageProfile();
				if (fileProfile != null) {
					newImageProfileName = getSaltString().concat(fileProfile.getOriginalFilename());
					try {
						Files.copy(fileProfile.getInputStream(), this.root.resolve(newImageProfileName));
					} catch (Exception e) {
						throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
					}
				}
				
				article.setProvider(provider);
				article.setPrice(price);
				article.setLabel(label);
				article.setImageFace(newImageFaceName);
				article.setImageProfile(newImageProfileName);
				//article.setPictureFace(articleRequest.getPictureFace());
				//article.setPictureProfile(articleRequest.getPictureProfile());
				return articleRepository.save(article);
			}).orElseThrow(() -> new ResourceNotFoundException("ArticleId " + articleId + "not found"));
		}).orElseThrow(() -> new ResourceNotFoundException("ProviderId " + providerId + " not found"));

	}
	
//	public Article delete(Long articleId) {
//		return articleRepository.findById(articleId).map(provider -> {
//			articleRepository.delete(provider);
//			return provider;
//	
//		}).orElseThrow(() -> new ResourceNotFoundException("ArticleId " + articleId + " not found"));
//	}

//	public Article delete(Long articleId) {
//		return articleRepository.findById(articleId).map(article -> {
//			articleRepository.delete(article);
//			return article;
//	
//		}).orElseThrow(() -> new ResourceNotFoundException("ArticleId " + articleId + " not found"));
//	}
	
	public Article deleteArticle(Long articleId) {
        return articleRepository.findById(articleId).map(article -> {
        	File f_face = new File(this.root + "/" + article.getImageFace()); // file to be delete
        	f_face.delete();
        	File f_profile = new File(this.root + "/" + article.getImageProfile()); // file to be delete
        	f_profile.delete();
            articleRepository.delete(article);
            return article;
        }).orElseThrow(() -> new ResourceNotFoundException("Article not found with id " + articleId));
    }
	
	public Article findOneArticle(long id) {
		return articleRepository.findById(id).get();
	}
}
