package com.company.inventory.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.inventory.dao.ICategoryDao;
import com.company.inventory.dao.IProductDao;
import com.company.inventory.model.Category;
import com.company.inventory.model.Product;
import com.company.inventory.response.ProductResponseRest;
import com.company.inventory.util.Util;

@Service
public class ProductServiceImpl implements IProductService {

	private ICategoryDao  categoryDao;
	private IProductDao productoDao;
	
	
	public ProductServiceImpl(ICategoryDao categoryDao, IProductDao productDao) {
		super();
		this.categoryDao = categoryDao;
		this.productoDao = productDao;
	}
	

	@Override
	@Transactional
	public ResponseEntity<ProductResponseRest> save(Product product, Long categoryId) {
		
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list = new ArrayList<>();
		
		try {
			
			//Search category to set in the product object
			
			Optional<Category> category = categoryDao.findById(categoryId);
			
			
			if(category.isPresent()) {
				product.setCategory(category.get());
			} else {
				response.setMetadata("Respuesta NO ok", "-1", "Categoria no encontrada asociada al producto");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}
			
			//Save the product 
			
			Product productSaved = productoDao.save(product);
			
			if(productSaved != null) {
				
				list.add(productSaved);
				response.getProduct().setProducts(list);
				response.setMetadata("Respuesta ok", "00", "Producto guardado");
			} else {
				response.setMetadata("Respuesta NO ok", "-1", "Producto no guardado");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.BAD_REQUEST);
			}
			
		} catch (Exception e) {
			
			e.getStackTrace();
			response.setMetadata("Respuesta NO ok", "-1", "Error al guardar producto");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
		
		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
		
		
		
		
	}


	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ProductResponseRest> searchById(Long id) {
		
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list = new ArrayList<>();
		
		try {
			
			//Search product by ID
			
			Optional<Product> product = productoDao.findById(id);
			
			
			if(product.isPresent()) {
				
				byte [] imagenDescompressed = Util.decompressZLib(product.get().getPicture());
				product.get().setPicture(imagenDescompressed);
				list.add(product.get());
				response.getProduct().setProducts(list);
				response.setMetadata("Respuesta OK", "00", "Producto Encontrado");
				
				
			} else {
				response.setMetadata("Respuesta NO ok", "-1", "producto no encontrado");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}
			

			
		} catch (Exception e) {
			
			e.getStackTrace();
			response.setMetadata("Respuesta NO ok", "-1", "Error al guardar producto");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
		
		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
	}


	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ProductResponseRest> searchByName(String name) {

		ProductResponseRest response = new ProductResponseRest();
		List<Product> list = new ArrayList<>();
		List<Product> listAux = new ArrayList<>();
		
		try {
			
			//Search product by name
			
			listAux = productoDao.findByNameContainingIgnoreCase(name);
			
			
			if(listAux.size() > 0) {
				
				listAux.stream().forEach((p) -> {
					byte [] imagenDescompressed = Util.decompressZLib(p.getPicture());
					p.setPicture(imagenDescompressed);
					list.add(p);
				});
				
				response.getProduct().setProducts(list);
				response.setMetadata("Respuesta OK", "00", "Productos Encontrados");
				
				
			} else {
				response.setMetadata("Respuesta NO ok", "-1", "productos no encontrados");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}
			

			
		} catch (Exception e) {
			
			e.getStackTrace();
			response.setMetadata("Respuesta NO ok", "-1", "Error al buscar producto por nombre");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
		
		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
	}


	@Override
	@Transactional
	public ResponseEntity<ProductResponseRest> deleteById(Long id) {
		ProductResponseRest response = new ProductResponseRest();
		
		try {
			
			//delete product by ID
			
			productoDao.deleteById(id);
			response.setMetadata("Respuesta OK", "00", "Producto Eliminado");

			

			
		} catch (Exception e) {
			
			e.getStackTrace();
			response.setMetadata("Respuesta NO ok", "-1", "Error al eliminar producto");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
		
		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
	}


	@Override
	@Transactional(readOnly= true)
	public ResponseEntity<ProductResponseRest> search() {
		ProductResponseRest response = new ProductResponseRest();
		List<Product> list = new ArrayList<>();
		List<Product> listAux = new ArrayList<>();
		
		try {
			
			//Search product 
			
			listAux = (List<Product>) productoDao.findAll();
			
			
			if(listAux.size() > 0) {
				
				listAux.stream().forEach((p) -> {
					byte [] imagenDescompressed = Util.decompressZLib(p.getPicture());
					p.setPicture(imagenDescompressed);
					list.add(p);
				});
				
				response.getProduct().setProducts(list);
				response.setMetadata("Respuesta OK", "00", "Productos Encontrados");
				
				
			} else {
				response.setMetadata("Respuesta NO ok", "-1", "productos no encontrados");
				return new ResponseEntity<ProductResponseRest>(response, HttpStatus.NOT_FOUND);
			}
			

			
		} catch (Exception e) {
			
			e.getStackTrace();
			response.setMetadata("Respuesta NO ok", "-1", "Error al buscar productos");
			return new ResponseEntity<ProductResponseRest>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
		
		
		return new ResponseEntity<ProductResponseRest>(response, HttpStatus.OK);
	}
	

}
