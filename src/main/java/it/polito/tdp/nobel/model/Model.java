package it.polito.tdp.nobel.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.tdp.nobel.db.EsameDAO;

public class Model {

	private List<Esame> esami;
	//tiene traccia della media migliore per ora incontrata e quindi inizializzandola
	//a zero per sovrascriverla appena arrivo ad una soluzione
	private double bestMedia;
	//la soluzione migliore sara' un set di esame
	private Set<Esame> bestSoluzione;
	
	//importiamo tutti gli esami dal database
	public Model () {
		EsameDAO dao = new EsameDAO();
		this.esami = dao.getTuttiEsami();
	}
	
	public void resetMedia() {
		bestMedia=0.0;
	}
	
	//procedura iniziale che richiama quella ricorsiva
	public Set<Esame> calcolaSottoinsiemeEsami(int numeroCrediti) {
		
		bestSoluzione=new HashSet<>();
		Set<Esame> parziale = new HashSet<>();
		
		//il parametro in mezzo e' il livello
		cerca1(parziale, 0, numeroCrediti);
		
		return bestSoluzione;
	}
	
	/*
	 	Dunque questo approccio e' quello piu' efficiente tra questo e quello che viene presentato in seguito in 
	 	quanto qui scorriamo l'elenco di esami che abbiamo importato in ordine ed evitiamo cosi' di riconsiderare
	 	stesse combinazioni, ma solo in ordine differente sprecando una grande quantita' di tempo.
	 	In questo approccio per ogni esame che scorriamo nell'elenco andiamo a richiamare due metodi di chiamata 
	 	ricorsiva in cui nel primo siamo andati ad inserire l'esame considerato nella soluzione parziale, mentre
	 	nell'altra strada abbiamo proseguito senza inserirlo passando al livello successivo e quindi direttamente
	 	al prossimo esame da controllare.
	 */
	/* APPROCCIO 1*/
	/* Complessità : 2^N */
	private void cerca1(Set<Esame> parziale, int L, int m) {
		
		//casi terminali
		//funzione creata che somma tutti i crediti degli esami che abbiamo
		//per ora salvati in parziale
		int crediti = sommaCrediti(parziale);
		//se la parziale che abbiamo per ora generato ha un numero di crediti
		//superiore alla soglia, torniamo al passo precedente della ricorsione 
		//provando magari ad inserire un nuovo esame
		if(crediti > m)
			return;
		
		//calcoliamo la media della nostra soluzione attuale e la confrontiamo
		//con la migliore media fino ad ora trovata
		if(crediti == m) {
			
			double media = calcolaMedia(parziale);
			if(media > bestMedia) {
				//sovrascrivo la media su quella migliore
				
				//questo qui sotto e' un erroraccio perche' clono il riferimento cosa che non bisogna mai
				//fare ma bisogna sempre crearsi nuovi oggetti
				//bestSoluzione=parziale; 
				bestSoluzione = new HashSet<>(parziale);
				bestMedia = media;
			}
		}
		
		//sicuramente, crediti < m se siamo arrivati qui
		
		//ultimo caso da verificare prima di scendere in livelli successivi
		//e' controllare che non siamo gia' alla fine della lista di esami da controllare
		//e quindi non abbiamo piu' nessun esame da andare a controllare
		if(L == esami.size()) {
			return ;
		}
		
		
		//generiamo i sotto-problemi
		//esami[L] è da aggiungere o no? Provo entrambe le cose
		
		//secondo me qui e' un po' una cagata come l'ha fatto
		//perche' invece di provare se aggiungo o no e scendo in entrambe
		//le strade, io se con l'aggiunta dell'esame avessi superato i crediti
		//di soglia, non sarei direttamente andato nell'approccio ricorsivo. Avrei
		//risparmiato direttamente la chiamata ricorsiva, pero' siccome nella ricorsiva e' poi la prima cosa
		//che vado a controllare alla fine non e' che a livello di tempo avremmo risparmiato chissa' cosa.
		
		//provo ad aggiungerlo
		parziale.add(esami.get(L));
		cerca1(parziale, L+1,m);
		parziale.remove(esami.get(L));
		
		//provo la strada di non aggiungerlo
		cerca1(parziale, L+1, m);
		
		
	}
	
	/*
	 	Questo approccio e' decisamente meno efficiente perche' qui non andiamo a scorrere in ordine gli esami 
	 	nell'elenco che abbiamo importato, ma ogni volta li scorriamo tutti e quindi se non sono ancora 
	 	stati considerati li andiamo ad inserire nella soluzione parziale. Il problema e' che con questa struttura
	 	nel momento in cui ad ogni livello scorriamo esami non ancora inseriti nella soluzione parziale, potremmo
	 	andare ad inserire nella soluzione parziale, esami che nell'elenco originario sono precedenti all'ultimo
	 	inserito e quindi andiamo a creare tutte le possibili permutazioni di esami andando a considerare esami che
	 	sono gia' stati considerati.
	 	Si potrebbe mettere un controllo che dato l'ultimo esame inserito io possa scorrere tutti gli esami dell'elenco
	 	originario solo successivi all'ultimo inserito e creerei una soluzione equivalente a quella precedente a 
	 	livello computazionale.
	 */
	/* APPROCCIO 2 */
	/* Complessità : N! */
	private void cerca2(Set<Esame> parziale, int L, int m) {
		//casi terminali
		
		int crediti = sommaCrediti(parziale);
		if(crediti > m)
			return;
				
		if(crediti == m) {
			double media = calcolaMedia(parziale);
			if(media > bestMedia) {
				bestSoluzione = new HashSet<>(parziale);
				bestMedia = media;
			}
		}
				
		//sicuramente, crediti < m
		if(L == esami.size()) {
			return ;
		}
		
		//generiamo i sotto-problemi
		for(Esame e : esami) {
			if(!parziale.contains(e)) {
				parziale.add(e);
				cerca2(parziale, L + 1, m);
				parziale.remove(e);
			}
		}
	}

	//lo definisce pubblico per poterlo cosi' richiamare anche dal controllore
	//e fare stampare la media
	public double calcolaMedia(Set<Esame> parziale) {
		int crediti = 0;
		int somma = 0;
		
		for(Esame e : parziale){
			crediti += e.getCrediti();
			somma += (e.getVoto() * e.getCrediti());
		}
		
		return somma/crediti;
	}

	private int sommaCrediti(Set<Esame> parziale) {
		int somma = 0;
		
		for(Esame e : parziale)
			somma += e.getCrediti();
		
		return somma;
	}

}
