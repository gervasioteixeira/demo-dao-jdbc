package application;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {

		SellerDao sellerDao = DaoFactory.createSellerDao();
		
		System.out.println("=== TESTE 1: Encontrar vendedor por ID ===");
		Seller seller = sellerDao.findById(3);
		System.out.println(seller);
		
		System.out.println("\n=== TESTE 2: Encontrar vendedor por Departamento ===");
		Department dep = new Department(2,null);
		List<Seller> list = sellerDao.findByDepartment(dep);
		for(Seller s : list) {
			System.out.println(s);
		}
	}

}
