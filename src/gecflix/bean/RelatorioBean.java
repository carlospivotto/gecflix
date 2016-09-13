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

		System.out.println("Gerando relatório em PDF");

		/* 
		 * Caminho onde ficarão nossos recursos. 
		 * Ele pode ser gerenciado melhor, criado, por exemplo, como constante, em um projeto maior.
		 */
		String path = "D:\\workspace\\gecflix\\";

		/*
		 * O arquivo JRXML é só um XML. Ele precisa ser compilado para um .jasper. Isto também é feito pelo próprio iReport.
		 * Você pode optar por manter o arquivo .jasper sempre à mão, para pular esta etapa.
		 */
		JasperCompileManager.compileReport(path + "gecflix_relatorio.jrxml");

		/*
		 * Criamos abaixo uma conexão, de onde o Jasper tirará os dados persistidos:
		 */
		Connection conn = new ConnectionFactory().getConnection();

		/* Para imprimir o PDF na tela, precisamos passá-lo no objeto Response
		 * (Lembre-se da dinâmica Request-Response) 
		 */
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
				.getResponse();
		ServletOutputStream out = response.getOutputStream();
		response.setHeader("Cache-Control", "max-age=0");
		response.setContentType("application/pdf");
		/* */

		/*
		 * Vamos agora fornecer os dados ao arquivo compilado.
		 * O parâmetro do meio do método, aqui passado como null, 
		 * pode conter parâmetros (por exemplo, filtros) que usaríamos no relatório.
		 */
		JasperPrint print = JasperFillManager.fillReport(path + "gecflix_relatorio.jasper", null, conn);

		/* 
		 * O objeto ByteArrayOutputStream é o fluxo de informações que será, por fim, escrito na tela com o response. 
		 */
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		/*
		 * Exportamos o objeto print, que é o jasper compilado + informações, com o array de bytes. 
		 * Isto será exibido na tela mais adiante.
		 */
		JasperExportManager.exportReportToPdfStream(print, baos);

		/*
		 * Uma alternativa é escrever o PDF em arquivo. 
		 * Aqui, exibiremos em tela e salvaremos em arquivo ao mesmo tempo.
		 * A linha abaixo cria outro stream de saída. 
		 * O relatório ficará na pasta do .jasper e do .jrxml e terá como nome 'relatorio' seguido do timestamp de sua criação. 
		 */
		OutputStreamExporterOutput outputStream = new SimpleOutputStreamExporterOutput(
				path + "relatorio-" + new Date().getTime() + ".pdf");

		/*
		 * As quatro próximas linhas escrevem o arquivo. 
		 */
		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setExporterInput(new SimpleExporterInput(print));
		exporter.setExporterOutput(outputStream);
		exporter.exportReport();

		
		/*
		 * As informações exibidas em tela:
		 * Escreveremos o fluxo de saída, out
		 * 
		 */
		out.write(baos.toByteArray());
		//out.flush();
		out.close();
		/* */

		conn.close();
	}
}
