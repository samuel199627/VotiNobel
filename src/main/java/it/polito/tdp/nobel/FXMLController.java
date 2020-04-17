package it.polito.tdp.nobel;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import it.polito.tdp.nobel.model.Esame;
import it.polito.tdp.nobel.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


//In questo programma importiamo da un database un elenco di esami e dobbiamo cercare, dati un certo numero di crediti
//la migliore combinazione possibile di esami che danno quei crediti e che danno la media pesata piu' alta.
//Quindi dobbiamo adottare una misura ricorsiva in cui esplorare tutte le possibili soluzioni e restituire un'unica
//combinazione che e' quella con la migliore media che troviamo che rispetta la combinazione di crediti.

public class FXMLController {

	Model model;
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField txtInput;
    
    @FXML
    private TextArea txtResult;

    @FXML
    private Button btnReset;

    @FXML
    void doCalcolaCombinazione(ActionEvent event) {
    	txtResult.clear();
    		try {
    			int numeroCrediti = Integer.parseInt(txtInput.getText());
    			
    			Long start = System.currentTimeMillis();
    			//potevo anche inserirlo direttamente nel modello nella funzione che richiama la parte ricorsiva
    			//ma l'ho messo qui per evidenziare che lui aveva dimenticato questa parte e quindi senza riazzerare la media
    			//tutti i calcoli delle soluzioni venivano sballati.
    			//In piu' anche il vettore di soluzione va inizializziamo ad ogni volta che entriamo nella funziona che richiama
    			//quella ricorsiva e anche quello ho dovuto aggiungerlo io
    			model.resetMedia();
    			Set<Esame> voti = new HashSet<>();
    			voti = model.calcolaSottoinsiemeEsami(numeroCrediti);
    			Long end = System.currentTimeMillis();
    			
    			
    			if(voti == null) {
    				txtResult.appendText("Non ho trovato soluzioni\n");
    				return ;
    			}
    			
    			txtResult.appendText("TEMPO IMPIEGATO: " + (end-start) + " ms\n");
    			txtResult.appendText("MEDIA: " + this.model.calcolaMedia(voti) + "\n");
    			for(Esame e : voti) {
    				txtResult.appendText(e.toString() + "\n");
    			}
    			
    		} catch (NumberFormatException e) {
    			txtResult.setText("Inserire un numero di crediti > 0");
    		}
    }

    @FXML
    void doReset(ActionEvent event) {
    		// reset the UI
    		txtInput.clear();
    		txtResult.clear();
    }

    @FXML
    void initialize() {
        assert txtInput != null : "fx:id=\"txtInput\" was not injected: check your FXML file 'VotiNobel.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'VotiNobel.fxml'.";
        assert btnReset != null : "fx:id=\"btnReset\" was not injected: check your FXML file 'VotiNobel.fxml'.";
    }

	public void setModel(Model model) {
		
		this.model = model;
	}
}
