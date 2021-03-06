package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {

	private Connection conn;

	public SellerDaoJDBC(Connection conn) {// a conex?o vem do DaoFactory
		this.conn = conn;
	}

	@Override
	public void insert(Seller seller) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("INSERT INTO seller " + "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES " + "(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

			st.setString(1, seller.getName());
			st.setString(2, seller.getEmail());
			st.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			st.setDouble(4, seller.getBaseSalary());
			st.setInt(5, seller.getDepartment().getId());

			int linhasAfetadas = st.executeUpdate();

			if (linhasAfetadas > 0) {// significa que inseriu um valor
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);// valor do id gerado pelo BD
					seller.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Erro: nenhuma linha inserida");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Seller seller) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " + "WHERE Id = ?");

			st.setString(1, seller.getName());
			st.setString(2, seller.getEmail());
			st.setDate(3, new java.sql.Date(seller.getBirthDate().getTime()));
			st.setDouble(4, seller.getBaseSalary());
			st.setInt(5, seller.getDepartment().getId());
			st.setInt(6, seller.getId());

			st.executeUpdate();

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}

	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
			ps.setInt(1, id);
			
			int linhaAfetada = ps.executeUpdate();
			
			if(linhaAfetada==0) {
				throw new DbException("Usu?rio n?o encontrado para dele??o");
			}

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(ps);
		}

	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE seller.Id = ?");

			st.setInt(1, id);

			rs = st.executeQuery();

			if (rs.next()) {
				Department d = instantiateDepartment(rs);
				Seller seller = instantiateSeller(rs, d);

				return seller;
			}
			return null;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
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
		d.setId(rs.getInt("DepartmentId"));// nome da coluna no BD que cont?m o id do departamento
		d.setName(rs.getString("DepName"));

		return d;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "ORDER BY Name");

			rs = st.executeQuery();
			List<Seller> list = new ArrayList<>();

			Map<Integer, Department> map = new HashMap<>(); // Para controlar a n?o repeti??o do departamento (para n?o
															// instanciar um departamento diferente para cada inst?ncia
															// de funcionario)

			while (rs.next()) {
				Department depmap = map.get(rs.getInt("DepartmentID"));// verifica se no map j? existe o departamento
																		// cadastrado (busca pelo id que vem pelo
																		// ResultSet)

				if (depmap == null) {
					depmap = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentID"), depmap);
				}

				Seller seller = instantiateSeller(rs, depmap);

				list.add(seller);

			}
			return list;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}

	}

	@Override
	public List<Seller> findByDepartment(Department dep) {

		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE DepartmentId = ? " + "ORDER BY Name");

			st.setInt(1, dep.getId());

			rs = st.executeQuery();
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>(); // Para controlar a n?o repeti??o do departamento (para n?o
															// instanciar um departamento diferente para cada inst?ncia
															// de funcionario)

			while (rs.next()) {
				Department depmap = map.get(rs.getInt("DepartmentID"));// verifica se no map j? existe o departamento
																		// cadastrado (busca pelo id que vem pelo
																		// ResultSet)

				if (depmap == null) {
					depmap = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentID"), depmap);
				}

				Seller seller = instantiateSeller(rs, depmap);

				list.add(seller);

			}
			return list;

		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeResultSet(rs);
			DB.closeStatement(st);
		}
	}

}
