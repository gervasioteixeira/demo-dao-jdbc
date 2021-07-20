package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao{

	private Connection conn;
	
	public SellerDaoJDBC(Connection conn) {//a conexão vem do DaoFactory
		this.conn=conn;
	}
	
	@Override
	public void insert(Seller seller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Seller seller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st=conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE seller.Id = ?");
			
			st.setInt(1, id);
			
			rs = st.executeQuery();
			
			if(rs.next()) {
				Department d = instantiateDepartment(rs);
				Seller seller = instantiateSeller(rs,d);
				
				return seller;
			}
			return null;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

	private Seller instantiateSeller(ResultSet rs, Department d) throws SQLException {
		Seller s = new Seller();
		s.setId(rs.getInt("Id"));
		s.setName(rs.getString("Name"));
		s.setEmail(rs.getString("Email"));
		s.setBaseSalary(rs.getDouble("BaseSalary"));
		s.setBirthDate(rs.getDate("BirthDate"));
		s.setDepartment(d);
		
		return s;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department d = new Department();
		d.setId(rs.getInt("DepartmentId"));//nome da coluna no BD que contém o id do departamento
		d.setName(rs.getString("DepName"));
		
		return d;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st=conn.prepareStatement("SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "ORDER BY Name");
				
			rs = st.executeQuery();
			List <Seller> list = new ArrayList<>();
			
			Map<Integer, Department> map =new HashMap<>(); //Para controlar a não repetição do departamento (para não instanciar um departamento diferente para cada instância de funcionario)
			
			while(rs.next()) {
				Department depmap = map.get(rs.getInt("DepartmentID"));//verifica se no map já existe o departamento cadastrado (busca pelo id que vem pelo ResultSet)
				
				if(depmap==null) {
					depmap=instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentID"), depmap);
				}
				
				Seller seller = instantiateSeller(rs,depmap);
				
				list.add(seller);
				
			}
			return list;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}

	}

	@Override
	public List<Seller> findByDepartment(Department dep) {
		
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			st=conn.prepareStatement("SELECT seller.*,department.Name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.DepartmentId = department.Id "
					+ "WHERE DepartmentId = ? "
					+ "ORDER BY Name");
			
			st.setInt(1, dep.getId());
			
			rs = st.executeQuery();
			List <Seller> list = new ArrayList<>();
			Map<Integer, Department> map =new HashMap<>(); //Para controlar a não repetição do departamento (para não instanciar um departamento diferente para cada instância de funcionario)
			
			while(rs.next()) {
				Department depmap = map.get(rs.getInt("DepartmentID"));//verifica se no map já existe o departamento cadastrado (busca pelo id que vem pelo ResultSet)
				
				if(depmap==null) {
					depmap=instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentID"), depmap);
				}
				
				Seller seller = instantiateSeller(rs,depmap);
				
				list.add(seller);
				
			}
			return list;
			
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		}finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

}
