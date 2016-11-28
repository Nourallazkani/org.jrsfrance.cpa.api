package org.sjr.babel.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

//@WebServlet(urlPatterns="/rest/*")
public class SpringLikeSimpleServlet extends javax.servlet.http.HttpServlet {

	private ObjectMapper jackson4Json = new ObjectMapper();
	
	
	private ApplicationContext ctx;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		this.ctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
	}

	private Method getMethodToInvoke(HttpServletRequest req){
		String path = req.getPathInfo();
		String[] pathParts = path.split("/");
		
		Collection<Object> restControllers = this.ctx.getBeansWithAnnotation(RestController.class).values();
		
		for(Object restController : restControllers){
			for(Method method : restController.getClass().getMethods()){
				if(method.isAnnotationPresent(RequestMapping.class)){
					RequestMapping rm = method.getAnnotation(RequestMapping.class);
					String rmPath = rm.path()[0];
					String[] rmPathParts = rmPath.split("/");
					if(rmPathParts.length == pathParts.length){
						boolean pathMatch = IntStream.range(0, rmPathParts.length)
								.allMatch(i-> (rmPathParts[i].startsWith("{") && rmPathParts[i].endsWith("}")) || rmPathParts[i].equals(pathParts[i]));
						if(pathMatch){
							boolean verbMath = Stream.of(rm.method()).anyMatch(m -> m.name().equals(req.getMethod()));
							if(verbMath){
								return method;
							}
						}
					}
				}
			}
		}
		return null;
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		
		Method methodToInvoke = getMethodToInvoke(req);
		Parameter[] methodParameters = methodToInvoke.getParameters();
		Object[] argsForMethodToInvoke = new Object[methodParameters.length];
		for (int i = 0; i < methodParameters.length; i++) {
			Parameter p = methodParameters[i];
			// paramÃ¨tre dont le type est connu Ã  l'avance par Spring
			if(p.getType().equals(HttpServletRequest.class)){
				argsForMethodToInvoke[i] = req;
			}
			else if(p.getType().equals(InputStream.class)){
				argsForMethodToInvoke[i] = req.getInputStream();
			}
			else if(p.getType().equals(HttpServletResponse.class)){
				argsForMethodToInvoke[i] = resp;
			}
			else if(p.getType().equals(OutputStream.class)){
				argsForMethodToInvoke[i] = resp.getOutputStream();
			}
			else if(p.getType().equals(HttpSession.class)){
				argsForMethodToInvoke[i] = req.getSession();
			}
			
			// paramètre annotés @PathVariable int id
			else if(p.isAnnotationPresent(PathVariable.class)){
				String mappingPath = methodToInvoke.getAnnotation(RequestMapping.class).path()[0];
				 
				int pathVariableIndex = Arrays.asList(mappingPath.split("/")).indexOf("{"+p.getName()+"}");
				argsForMethodToInvoke[i] = req.getPathInfo().split("/")[pathVariableIndex];
			}
			else if(p.isAnnotationPresent(RequestParam.class)){
				argsForMethodToInvoke[i] = req.getParameter(p.getName());
			}
			else if(p.isAnnotationPresent(RequestHeader.class)){
				argsForMethodToInvoke[i] = req.getHeader(p.getName());
			}	
			else if(p.isAnnotationPresent(RequestBody.class)){
				String contentType = req.getHeader("content-type");
				if("application/json".equalsIgnoreCase(contentType)){
					Object requestBody = jackson4Json.readValue(req.getInputStream(), p.getType());
					argsForMethodToInvoke[i] = requestBody;
				}
				else{
					resp.setStatus(406); // NOT ACCEPTED
					return;
				}
			}
			else{
                try {
                	Object o = p.getType().newInstance();
	                Set<Entry<String, String[]>> params = req.getParameterMap().entrySet();
	                for (Entry<String, String[]> entry : params) {
	                    	String paramName = entry.getKey();
							Object paramValue = entry.getValue();
							// valorisation de la propriété dont le chemin est paramName avec la valeur lue dans paramValue
	                }
	                argsForMethodToInvoke[i] = o;
                }
	            catch(Exception e){
	            	// p.getType() is an interface or is an abstract class or does not have a 0 args constructor
	                argsForMethodToInvoke[i] = null;
                }
			}
			
			Object controller = this.ctx.getBean(methodToInvoke.getDeclaringClass());
			try {
				// if method has void return type
				if(methodToInvoke.getReturnType().equals(Void.class)){
					methodToInvoke.invoke(controller, argsForMethodToInvoke);
					resp.setStatus(200);
					return;
				}
				// if method returns something
				Object ret = methodToInvoke.invoke(controller, argsForMethodToInvoke);
				if(ret == null){
					resp.setStatus(200);
				}
				else if(ret instanceof ResponseEntity){
					ResponseEntity<?> re = (ResponseEntity<?>)ret;
					resp.setStatus(re.getStatusCode().value());
					
					Object body = re.getBody();
					if(body!=null){
						String accept = req.getHeader("accept");
						if("application/json".equals(accept)){
							jackson4Json.writeValue(resp.getOutputStream(), body);
						}
						else{
							resp.setStatus(406); // NOT ACCEPTED
							return;
						}
					}
				}
				else{
					resp.setStatus(200);
					Object body = ret;
					if(body!=null){
						String accept = req.getHeader("accept");
						if("application/json".equals(accept)){
							jackson4Json.writeValue(resp.getOutputStream(), body);
						}
						else{
							resp.setStatus(406); // NOT ACCEPTED
							return;
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
}

