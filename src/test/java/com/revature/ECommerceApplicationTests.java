package com.revature;

import com.revature.models.Product;
import com.revature.models.User;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.restassured.RestAssured;
import io.restassured.response.Response;


@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ECommerceApplication.class)

// TED NOTES
// Session attribute
// Test service methods called from the controller
// https://www.baeldung.com/karate-rest-api-testing


// APPLICATION TESTS (aka controller tests)
// These tests should only check if the controller endpoints return an OK response (for now)

class ECommerceApplicationTests {

	private static final String API_ROOT = "http://localhost:5000/";
	//

	private Product getExistingProduct(){
		Product product = new Product();
		product.setId(1);
		product.setQuantity(25);
		product.setPrice(19.99);
		product.setDescription("test product");
		product.setImage("random pic");
		product.setName("Test");
		product.setFeatured(true);
		product.setSale(25.0);
		return product;
	}

	private User getExistingUser() {
		// Since there is not an endpoint for getting user info. This user should already be in the database already.
		User user = new User();
		user.setPassword("user");
		user.setEmail("user@user.com");
		return user;
	}

	@Test
	void contextLoads() {
	}

	// AUTH TESTS =======================================================================

	/*
	@Test public void whenLogInUserExist_thenOK() {
		final User user = getExistingUser();
		Response logInResponse = logIn(user);
		//String email = logInResponse.jsonPath().get("email");
		//String password = logInResponse.jsonPath().get("password");
		assertEquals(HttpStatus.OK.value(), logInResponse.getStatusCode());
		// Needs to return JSON with email and password for this to work.
		//assertEquals(user.getEmail(), email);
		//assertEquals(user.getPassword(), password);
	}*/

	@Test public void whenLogInUserNotExist_thenBAD_REQUEST() { // Change bad request to something else later?
		final User user = getExistingUser();
		user.setPassword("wrongPassword");
		Response logInResponse = logIn(user);

		assertEquals(HttpStatus.BAD_REQUEST.value(), logInResponse.getStatusCode()); //400
	}

	// PRODUCT TESTS =====================================================================

	@Test public void whenGetAllProductsNotLoggedIn_thenUNAUTHORIZED() {
		final Response response = RestAssured.get(API_ROOT + "/api/product");
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
	}

	@Test public void whenGetAllProductsLoggedIn_thenOK() {
		final User user = getExistingUser();
		Response logInResponse = logIn(user);
		final Response response = RestAssured.get(API_ROOT + "/api/product");
		assertEquals(HttpStatus.OK.value(), logInResponse.getStatusCode(), response.getStatusCode());
	}

	@Test public void whenGetProductByID_thenOK() {
		final User user = getExistingUser();
		Response logInResponse = logIn(user);

		final Product product = getExistingProduct();
		final Response response = RestAssured.get(API_ROOT + "/api/product/" + product.getId());

		assertEquals(HttpStatus.OK.value(), logInResponse.getStatusCode(), response.getStatusCode());
	}

	@Test public void whenGetAllFeaturedProductsLoggedIn_thenOK() {
		final User user = getExistingUser();
		Response logInResponse = logIn(user);
		final Response response = RestAssured.get(API_ROOT + "/api/product/featured");
		assertEquals(HttpStatus.OK.value(), logInResponse.getStatusCode(), response.getStatusCode());
	}

	@Test public void whenGetAllSaleProductsLoggedIn_thenOK(){
		final User user = getExistingUser();
		Response logInResponse = logIn(user);
		final Response response = RestAssured.get(API_ROOT + "/api/product/sale");
		assertEquals(HttpStatus.OK.value(), logInResponse.getStatusCode(), response.getStatusCode());

	}


	// ===================================================================================
	private Response logIn(User user) {
		//Log in with user credentials returned by getExistingUser()
		final Response response = RestAssured.given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(user)
				.post(API_ROOT + "/auth/login");
		return response;
	}

}