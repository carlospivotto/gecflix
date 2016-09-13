package gecflix.bean;

import java.sql.SQLException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import gecflix.dao.DAO;
import gecflix.modelo.Artista;
import gecflix.modelo.Filme;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

@ManagedBean
@ViewScoped
public class FilmeBean {
	private Filme filme = new Filme();

	// #{filmeBean.artistaId} do formulário
	private Integer artistaId;

	public Integer getArtistaId() {
		return artistaId;
	}

	public void setArtistaId(Integer artistaId) {
		this.artistaId = artistaId;
	}

	public Filme getFilme() {
		return filme;
	}

	public void setFilme(Filme filme) {
		this.filme = filme;
	}
	
	public List<Filme> getFilmes(){
		return new DAO<Filme>(Filme.class).listar();
	}

	public void salvar() {
		System.out.println("Registrando filme " + this.filme.getTitulo());

		if (filme.getArtistas().isEmpty()) {
			throw new RuntimeException("O filme deve ter pelo menos um artista.");
		}

		new DAO<Filme>(Filme.class).adicionar(this.filme);

		// Para limpar o formulário após o envio.
		this.filme = new Filme();
	}

	public List<Artista> getArtistas() {
		return new DAO<Artista>(Artista.class).listar();
	}

	public List<Artista> getArtistasFilme() {
		return this.filme.getArtistas();
	}

	public void salvarArtista() {
		Artista artista = new DAO<Artista>(Artista.class).buscarId(this.artistaId);
		this.filme.adicionarArtista(artista);
	}
	
}
