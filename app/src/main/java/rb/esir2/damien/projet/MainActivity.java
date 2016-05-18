package rb.esir2.damien.projet;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class MainActivity extends Activity implements View.OnClickListener {
    private ImageView lampe1;
    private ImageView lampe2;
    private ImageView lampe3;
    private ImageView lampe4;
    private ImageView icone_connexions;
    private TextView connexion_serveur;
    private TextView connexion_knx;
    private Button activer_chenillard;
    private Button vitesse_plus;
    private Button vitesse_moins;
    private Button motif1;
    private Button motif2;
    private Button motif3;
    private Button motif_random;
    private Button activer_knx;
    private Button activer_server;
    boolean etat_chenillard;
    boolean etat_server;
    boolean etat_knx;

    // Création d'un premier webSocket et des informations de connexion
    private final WebSocketConnection mWebSocket = new WebSocketConnection();
    private static final String TAG = "projet.RB.ESIR2";
    private final String URI = "ws://192.168.1.114:8080/ControleMaison-RB/server";

    // Configuration de chaque méthode du webSocket
    private WebSocketHandler webSocketHandler = new WebSocketHandler() {
        @Override
        public void onOpen() {
            Toast.makeText(getApplicationContext(), "Ouverture de la connexion", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Status: Connected to " + URI);

            // Modification de l'écriture du bouton de connexion, changement de l'icone de connexion
            if (mWebSocket.isConnected()) {
                connexion_serveur.setText(R.string.connecte);
                etat_server = true;
                icone_connexions.setImageResource(R.drawable.connexion1);
            }

        }

        @Override
        public void onTextMessage(String msg) {
            String[] tab = msg.split("[,;:]+");

            // Dans le cas où c'est un envoi de toutes les données (1er envoi), utilisation d'une autre méthode
            if (tab[0].equals("alldatas")) {
                initData(tab);
            } else {
                // Pour chaque message reçu, on traite pour mettre à jour l'interface
                switch (tab[0]) {
                    case "lampe":
                        switch (tab[1]) {
                            case "1":
                                if (tab[2].equals("on")) {
                                    lampe1.setImageResource(R.drawable.lumiere_allume);
                                } else {
                                    lampe1.setImageResource(R.drawable.lumiere_eteinte);
                                }
                                break;
                            case "2":
                                if (tab[2].equals("on")) {
                                    lampe2.setImageResource(R.drawable.lumiere_allume);
                                } else {
                                    lampe2.setImageResource(R.drawable.lumiere_eteinte);
                                }
                                break;
                            case "3":
                                if (tab[2].equals("on")) {
                                    lampe3.setImageResource(R.drawable.lumiere_allume);
                                } else {
                                    lampe3.setImageResource(R.drawable.lumiere_eteinte);
                                }
                                break;
                            case "4":
                                if (tab[2].equals("on")) {
                                    lampe4.setImageResource(R.drawable.lumiere_allume);
                                } else {
                                    lampe4.setImageResource(R.drawable.lumiere_eteinte);
                                }
                                break;
                        }
                        break;

                    case "etatChenillard":
                        if (tab[1].equals("on")) {
                            activer_chenillard.setText(R.string.desactiver);
                            etat_chenillard = true;

                        } else if(tab[1].equals("off")){
                            activer_chenillard.setText(R.string.activer);
                            etat_chenillard = false;
                        }
                        break;
                    case "knx":
                        if(tab[1].equals("true")){
                            etat_knx = true;
                            connexion_knx.setText(R.string.connecte);
                        }
                        else if(tab[1].equals("false")){
                            etat_knx = false;
                            connexion_knx.setText(R.string.deconnecte);
                        }
                        break;
                }
            }
        }

        @Override
        public void onClose(int code, String reason) {
            Toast.makeText(getApplicationContext(), "Fermeture de la connexion", Toast.LENGTH_SHORT).show();
            connexion_serveur.setText(R.string.deconnecte);
            icone_connexions.setImageResource(R.drawable.connexion0);
            etat_server = false;
        }

    };

    private void start() {
        try {
            mWebSocket.connect(URI, webSocketHandler);
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
        }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupération de tous les boutons ou afficheurs
        lampe1 = (ImageView) findViewById(R.id.lampe1);
        lampe2 = (ImageView) findViewById(R.id.lampe2);
        lampe3 = (ImageView) findViewById(R.id.lampe3);
        lampe4 = (ImageView) findViewById(R.id.lampe4);

        icone_connexions = (ImageView) findViewById(R.id.icone_connexion);
        connexion_serveur = (TextView) findViewById(R.id.connexion_ecran);
        connexion_knx = (TextView) findViewById(R.id.connexion_knx);

        activer_knx = (Button) findViewById(R.id.button_knx);
        activer_knx.setOnClickListener(this);
        activer_server = (Button) findViewById(R.id.button_server);
        activer_server.setOnClickListener(this);

        activer_chenillard = (Button) findViewById(R.id.button_chenillard);
        activer_chenillard.setOnClickListener(this);

        vitesse_plus = (Button) findViewById(R.id.vitesse_plus);
        vitesse_plus.setOnClickListener(this);
        vitesse_moins = (Button) findViewById(R.id.vitesse_moins);
        vitesse_moins.setOnClickListener(this);

        motif1 = (Button) findViewById(R.id.motif1);
        motif1.setOnClickListener(this);
        motif2 = (Button) findViewById(R.id.motif2);
        motif2.setOnClickListener(this);
        motif3 = (Button) findViewById(R.id.motif3);
        motif3.setOnClickListener(this);
        motif_random = (Button) findViewById(R.id.motif_random);
        motif_random.setOnClickListener(this);

        etat_chenillard = false;
        etat_knx = false;
        etat_server = false;

        // Démarrage de la connexion avec le serveur
        start();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            // Bouton pour activer , désactiver le chenillard
            case R.id.button_chenillard:
                if(etat_chenillard && etat_server){
                    mWebSocket.sendTextMessage("chenillard:off");
                }
                else if(!etat_chenillard && etat_server){
                    mWebSocket.sendTextMessage("chenillard:on");
                }
                etat_chenillard = !etat_chenillard;
                break;

            // Bouton pour augmenter la vitesse
            case R.id.vitesse_plus:
                if(etat_server){
                    mWebSocket.sendTextMessage("vitesse:plus");
                }
                break;

            // Bouton pour diminuer la vitesse
            case R.id.vitesse_moins:
                if(etat_server){
                    mWebSocket.sendTextMessage("vitesse:moins");
                }
                break;

            // Bouton pour changer au motif 1
            case R.id.motif1:
                if(etat_server){
                    mWebSocket.sendTextMessage("motif:1");
                }
                break;

            // Bouton pour changer au motif 2
            case R.id.motif2:
                if(etat_server){
                    mWebSocket.sendTextMessage("motif:2");
                }
                break;

            // Bouton pour changer au motif 3
            case R.id.motif3:
                if(etat_server){
                    mWebSocket.sendTextMessage("motif:3");
                }
                break;

            // Bouton pour changer à un motif aléatoire
            case R.id.motif_random:
                Random rand = new Random();
                int nb = rand.nextInt(3);
                if(etat_server){
                    mWebSocket.sendTextMessage("motif:"+(nb+1));
                }
                break;

            // Bouton de connexion, déconnexion au serveur
            case R.id.button_server:
                if(etat_server){
                    mWebSocket.disconnect();
                }
                else if(!etat_server){
                    try {
                        mWebSocket.connect(URI, webSocketHandler);
                    } catch (WebSocketException e) {
                        e.printStackTrace();
                    }
                }
                break;

            // Bouton de connexion, déconnexion à la maquette KNX
            case R.id.button_knx:
                if(etat_knx && etat_server){
                    mWebSocket.sendTextMessage("knx:deconnect");
                }
                else if(!etat_knx && etat_server){
                    mWebSocket.sendTextMessage("knx:connect");
                }
                break;
        }

    }

    // Méthode pour initialiser l'interface au démarrage de l'application
    public void initData(String[] tab){
        for(int i=0; i<tab.length; i++){
            switch (tab[i]) {
                case "lampe":
                    switch (tab[i+1]){
                        case "1":
                            if(tab[i+2].equals("on")){
                                lampe1.setImageResource(R.drawable.lumiere_allume);
                            }
                            else {
                                lampe1.setImageResource(R.drawable.lumiere_eteinte);
                            }
                            break;
                        case "2":
                            if(tab[i+2].equals("on")){
                                lampe2.setImageResource(R.drawable.lumiere_allume);
                            }
                            else {
                                lampe2.setImageResource(R.drawable.lumiere_eteinte);
                            }
                            break;
                        case "3":
                            if(tab[i+2].equals("on")){
                                lampe3.setImageResource(R.drawable.lumiere_allume);
                            }
                            else {
                                lampe3.setImageResource(R.drawable.lumiere_eteinte);
                            }
                            break;
                        case "4":
                            if(tab[i+2].equals("on")){
                                lampe4.setImageResource(R.drawable.lumiere_allume);
                            }
                            else {
                                lampe4.setImageResource(R.drawable.lumiere_eteinte);
                            }
                            break;
                    }
                    break;

                case "etatChenillard":
                    if(tab[i+1].equals("on")){
                        etat_chenillard = true;
                        activer_chenillard.setText(R.string.desactiver);
                    }
                    else if(tab[i+1].equals("off")){
                        etat_chenillard = false;
                        activer_chenillard.setText(R.string.activer);
                    }
                    break;

                case "knx":
                    if(tab[i+1].equals("true")){
                        etat_knx = true;
                        connexion_knx.setText(R.string.connecte);
                    }
                    else if(tab[i+1].equals("false")){
                        etat_knx = false;
                        connexion_knx.setText(R.string.deconnecte);
                    }
                    break;
            }

        }
    }
}
