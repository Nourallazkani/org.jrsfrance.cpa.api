package org.sjr.babel.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.sjr.babel.SpringConfig;
import org.sjr.babel.model.entity.AbstractLearningProgram;
import org.sjr.babel.persistence.ObjectStore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.StringUtils;

@WebServlet(urlPatterns = "/cursus_")
public class CursusServlet extends HttpServlet {

	private ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
	
	protected void doGet(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
			throws javax.servlet.ServletException, java.io.IOException {
		getServletContext().getRequestDispatcher("/WEB-INF/views/cursus.jsp").forward(req, resp);
	};

	protected void doPost(javax.servlet.http.HttpServletRequest req, javax.servlet.http.HttpServletResponse resp)
			throws javax.servlet.ServletException, java.io.IOException {
		
		ObjectStore os = ctx.getBean(ObjectStore.class);
		System.out.println(os);
		StringBuffer query = new StringBuffer("select c from Cursus c where 0=0 ") ;
		String city = req.getParameter("city");
		Map<String, Object> args = new HashMap<>();
		if (StringUtils.hasText(city)) {
			args.put("city" , city);
			query.append(" and c.address.city like :city ");
		}
				
		List<AbstractLearningProgram> results = os.find(AbstractLearningProgram.class, query.toString(), args);
		req.setAttribute("results", results);
		getServletContext().getRequestDispatcher("/WEB-INF/views/cursus.jsp").forward(req, resp);
	};
	
	public static void main(String[] args) {
		System.out.println(Double.parseDouble("123.45"));
	}
}
