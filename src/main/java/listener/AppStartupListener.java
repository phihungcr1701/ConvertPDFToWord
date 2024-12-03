package listener;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import model.BO.convertBO;
import model.Bean.file;

@WebListener
public class AppStartupListener implements ServletContextListener {
	private convertBO convertBO = new convertBO();
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			List<file> files = convertBO.getAllFile();
			sce.getServletContext().setAttribute("files", files);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}
	
}
