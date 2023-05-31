//package com.feuji.propertyInvestment.controller;
//
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.feuji.propertyInvestment.serviceImplementation.PropertyOrdersServiceImpl;
//import com.feuji.propertyinvestment.entity.Customer;
//import com.feuji.propertyinvestment.entity.PropertyOrder;
//@RestController
//public class PropertyOrderController {
//	@Autowired(required=true)
//	private PropertyOrdersServiceImpl propertyOrderImpl;
//
//
//	@PostMapping(value="/saveorder/customer/{cid}/property/{pid}", produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<PropertyOrder> save(@PathVariable("pid") int pid,@PathVariable("cid") int cid,@RequestBody PropertyOrder orders) {
//		propertyOrderImpl.save (orders,cid,pid);
//		return ResponseEntity.ok().body(orders);
//	}
//	
//	@PutMapping(value="/saveorder/customer/{cid}/property/{pid}", produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<PropertyOrder> put(@PathVariable("pid") int pid,@PathVariable("cid") int cid,@RequestBody PropertyOrder orders) {
//		propertyOrderImpl.update(orders,cid,pid);
//		return ResponseEntity.ok().body(orders);
//		
//	}
//	@PutMapping(value="/sellorder/customer/{cid}/property/{pid}", produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<PropertyOrder> sell(@PathVariable("pid") int pid,@PathVariable("cid") int cid,@RequestBody PropertyOrder orders) {
//		propertyOrderImpl.update(orders,cid,pid);
//		return ResponseEntity.ok().body(orders);
//		
//	}
//	
//	@PutMapping(value="/order/sell",produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<PropertyOrder> sell(@RequestBody PropertyOrder orders) {
//		System.out.println(orders);
//		propertyOrderImpl.sell(orders);
//		return ResponseEntity.ok().body(orders);
//		
//	}
//	  
//	@GetMapping("bycustomer/{id}")
//	public List< PropertyOrder> get(@PathVariable int id){
//	List<PropertyOrder> orders=	propertyOrderImpl.getPropertyOrdersByQuerry(id);
//	System.out.println(orders);
//		return orders;
//	}
//	
//	@GetMapping(value ="/allorders" ,produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<List<PropertyOrder>> getAll(Customer customer) {
//		List<PropertyOrder> allOrders = propertyOrderImpl.getPropertyOrders();
//		
//		return ResponseEntity.ok().body(allOrders);
//
//
//	}
//	
//	
//	@DeleteMapping("/order/delete/{id}")
//	public void delete(@PathVariable int id)
//	{
//		propertyOrderImpl.delete(id);
//	}
//	@PutMapping("/order/sell/{id}")
//	public void sell(@PathVariable int id)
//	{  
//		propertyOrderImpl.sell(id);
//	}
//	
//	
////	@PutMapping("/saveorder/customer/{cid}/property/{pid}")
////	public ResponseEntity<PropertyOrder> updateOrder(@RequestBody PropertyOrder orders, @PathVariable int cid,
////			@PathVariable int pid) {
////		
//////		orders.setPropertyId(propertyimpl.findById(pid));
//////		
//////		orders.setCustomerId(customerOrderImpl.findById(cid));
////		propertyOrderImpl.update(orders,cid,pid);
////		
////		return ResponseEntity.ok().body(orders);
////
////	}
//	}


package com.feuji.propertyInvestment.controller;


import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.feuji.propertyInvestment.exception.InvalidInputException;
import com.feuji.propertyInvestment.serviceImplementation.CacheClass;
import com.feuji.propertyInvestment.serviceImplementation.PropertyOrdersServiceImpl;
import com.feuji.propertyinvestment.entity.PropertyOrder;
@RestController
@ControllerAdvice
public class PropertyOrderController {

	@Autowired(required=true)
	private PropertyOrdersServiceImpl propertyOrderImpl;
	
	
	@Autowired(required=true)
	private CacheClass cacheClass;
	

    @Autowired
    private JavaMailSender javaMailSender;
	
	public boolean updated=false;


	@PostMapping(value="/saveorder/customer/{cid}/property/{pid}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PropertyOrder> save(@PathVariable("pid") int pid,@PathVariable("cid") int cid,@RequestBody PropertyOrder orders) {
		updated=true;
		PropertyOrder latest= propertyOrderImpl.save(orders,cid,pid);
		sendMail(latest.getOrderId());
		return ResponseEntity.ok().body(orders);
	}
	
	@PutMapping(value="/saveorder/customer/{cid}/property/{pid}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PropertyOrder> put(@PathVariable("pid") int pid,@PathVariable("cid") int cid,@RequestBody PropertyOrder orders) {
		propertyOrderImpl.update(orders,cid,pid);
		updated=true;
		return ResponseEntity.ok().body(orders);
		
	}
	  
	@GetMapping("bycustomer/{id}")
	public List< PropertyOrder> get(@PathVariable int id){
	List<PropertyOrder> orders=	propertyOrderImpl.getPropertyOrdersByQuerry(id);
	System.out.println("orders by query\n"+orders);
		return orders;
	}
	 @GetMapping("/send-mail/{id}")
	    public void sendMail(@PathVariable int id) {
		 System.out.println("sdfhgjkdfghjfghjkdfghjkdfdghj");
	        SimpleMailMessage message = new SimpleMailMessage();
	        
	        message.setSubject("Test Email");
	        System.out.println(propertyOrderImpl.getPropertyOrders().stream().filter(o->id==o.getOrderId()));
	        PropertyOrder ord=propertyOrderImpl.getPropertyOrders().stream().filter(o->id==o.getOrderId()).toList().get(0);
	        message.setText(ord.toString());
	        message.setTo(ord.getCustomerId().getCustomerMail());
	        javaMailSender.send(message);
	        //return "Mail Sent!";
	    }
	 

	 
	
	@GetMapping(value ="/allorders" ,produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<PropertyOrder>> getAll() {
		long start=new Date().getTime();
		System.out.println(start);
		System.out.println("test for all");
		
		List<PropertyOrder> allOrders =propertyOrderImpl.getPropertyOrders();
		long end=new Date().getTime();
		System.out.println(end);
		System.out.println("diff in sec is "+(end-start));
		
		return ResponseEntity.ok().body(allOrders);
		

	}
	
	@DeleteMapping("/order/delete/{id}/{status}")
	public void delete(@PathVariable int id,@PathVariable String status)
	{
		updated=true;
		propertyOrderImpl.delete(id,status);
		
	}
	
	
	@DeleteMapping("/order/delete/{id}")
	public void delete(@PathVariable int id)
	{
		updated=true;
		propertyOrderImpl.delete(id);
		
	}
	
	
	
	@PutMapping(value="/order/sell",produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PropertyOrder> sell(@RequestBody PropertyOrder orders) {
		
		//System.out.println(orders.getOrderDate()+"orders iddd "+orders);
		
		if(orders.getNoOfUnits()>0 
				&&
				(
						orders.getNoOfUnits() <=  propertyOrderImpl.getOrder(orders.getOrderId()).getNoOfUnits())
				)
		{
		//System.out.println(orders);
			System.out.println("somethingggggggggggggggggg");
		propertyOrderImpl.sell(orders);
		sendMail(orders.getOrderId());
		
		return ResponseEntity.ok().body(orders);
		}
		else
		{
			System.out.println("error");
		throw new InvalidInputException("enter valid input");	
		}
			
	}
	
	@ExceptionHandler(value=InvalidInputException.class)
	public InvalidInputException exceptionHandler(InvalidInputException exception,  WebRequest request)
	{
		 return exception;
	}
	
	
	
	}
