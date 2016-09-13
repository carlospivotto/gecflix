package gecflix.bean;

import javax.faces.bean.ManagedBean;

import gecflix.dao.DAO;
import gecflix.modelo.Artista;

@ManagedBean
public class ArtistaBean {
	private Artista artista = new Artista();

	public Artista getArtista() {
		return artista;
	}

	public void salvar() {
		System.out.println("Registrando artista: " + this.artista.getNome());

		new DAO<Artista>(Artista.class).adicionar(this.artista);
		
		//Para limpar o formulário após o envio.
		this.artista = new Artista();
	}
}
