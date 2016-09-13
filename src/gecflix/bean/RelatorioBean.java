package gecflix.bean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import gecflix.dao.ConnectionFactory;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@ManagedBean
public class RelatorioBean {
	public void gerarRelatorioFilmes() throws SQLException, JRException, IOException {

		System.out.println("Gerando relat�rio em PDF");

		/* 
		 * Caminho onde ficar�o nossos recursos. 
		 * Ele pode ser gerenciado melhor, criado, por exemplo, como constante, em um projeto maior.
		 */
		String path = "D:\\workspace\\gecflix\\";

		/*
		 * O arquivo JRXML � s� um XML. Ele precisa ser compilado para um .jasper. Isto tamb�m � feito pelo pr�prio iReport.
		 * Voc� pode optar por manter o arquivo .jasper sempre � m�o, para pular esta etapa.
		 */
		JasperCompileManager.compileReport(path + "gecflix_relatorio.jrxml");

		/*
		 * Criamos abaixo uma conex�o, de onde o Jasper tirar� os dados persistidos:
		 */
		Connection conn = new ConnectionFactory().getConnection();

		/* Para imprimir o PDF na tela, precisamos pass�-lo no objeto Response
		 * (Lembre-se da din�mica Request-Response) 
		 */
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();
		ServletOutputStream out = response.getOutputStream();
		response.setHeader("Cache-Control", "max-age=0");
		response.setContentType("application/pdf");
		/* */

		/*
		 * Vamos agora fornecer os dados ao arquivo compilado.
		 * O par�metro do meio do m�todo, aqui passado como null, 
		 * pode conter par�metros (por exemplo, filtros) que usar�amos no relat�rio.
		 */
		JasperPrint print = JasperFillManager.fillReport(path + "gecflix_relatorio.jasper", null, conn);

		/* 
		 * O objeto ByteArrayOutputStream � o fluxo de informa��es que ser�, por fim, escrito na tela com o response. 
		 */
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		/*
		 * Exportamos o objeto print, que � o jasper compilado + informa��es, com o array de bytes. 
		 * Isto ser� exibido na tela mais adiante.
		 */
		JasperExportManager.exportReportToPdfStream(print, baos);

		/*
		 * Uma alternativa � escrever o PDF em arquivo. 
		 * Aqui, exibiremos em tela e salvaremos em arquivo ao mesmo tempo.
		 * A linha abaixo cria outro stream de sa�da. 
		 * O relat�rio ficar� na pasta do .jasper e do .jrxml e ter� como nome 'relatorio' seguido do timestamp de sua cria��o. 
		 */
		OutputStreamExporterOutput outputStream = new SimpleOutputStreamExporterOutput(
				path + "relatorio-" + new Date().getTime() + ".pdf");

		/*
		 * As quatro pr�ximas linhas escrevem o arquivo. 
		 */
		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setExporterInput(new SimpleExporterInput(print));
		exporter.setExporterOutput(outputStream);
		exporter.exportReport();

		
		/*
		 * As informa��es exibidas em tela:
		 * Escreveremos o fluxo de sa�da, out
		 * 
		 */
		out.write(baos.toByteArray());
		//out.flush();
		out.close();
		/* */

		conn.close();
	}
}
