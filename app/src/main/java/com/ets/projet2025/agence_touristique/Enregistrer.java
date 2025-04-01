package com.ets.projet2025.agence_touristique;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Enregistrer extends AppCompatActivity {

    private EditText textNom, textPrenom, textEmail, textmdp, textAge, textTelephone, textAdresse, textConfirmerMDP;
    private Button confirmer;
    private OkHttpClient client = new OkHttpClient();
    private ObjectMapper objectMapper = new ObjectMapper();
    private ActivityResultLauncher<Intent> launcher;
    private ActivityResultLauncher<String> permissionLauncher;
    private final String url = "http://10.0.2.2:3000/clients";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enregistrer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textNom = findViewById(R.id.textNomInscription);
        textPrenom = findViewById(R.id.textPrenomInscription);
        textEmail = findViewById(R.id.textCourrielInscription);
        textmdp = findViewById(R.id.textMDPInscription);
        textAge = findViewById(R.id.textAgeInscription);
        textTelephone = findViewById(R.id.textTelephoneInscription);
        textAdresse = findViewById(R.id.textAdresseInscription);
        textConfirmerMDP = findViewById(R.id.textMDPConfirmationInscription);
        confirmer = findViewById(R.id.btnConfirmer);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(Enregistrer.this, "Retour réussi", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        confirmer.setOnClickListener(v->{
            ajouterClient();
        });
    }

    private void ajouterClient(){
        String nom = textNom.getText().toString();
        String prenom = textPrenom.getText().toString();
        String email = textEmail.getText().toString();
        String mdp = textmdp.getText().toString();
        String confirmerMdp = textConfirmerMDP.getText().toString();
        String age = textAge.getText().toString();
        String telephone = textTelephone.getText().toString();
        String adresse = textNom.getText().toString();

        if(nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || mdp.isEmpty() || age.isEmpty() || telephone.isEmpty() || adresse.isEmpty() || confirmerMdp.isEmpty()){
            Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
            return;
        }

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(Enregistrer.this, "Échec de connexion.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String clientreponse = response.body().string();

                    List<Client> clients = objectMapper.readValue(clientreponse, new TypeReference<List<Client>>(){});

                    int idMax = 0;
                    for(int i = 0; i < clients.size();i++){
                        if(clients.get(i).getId() > idMax){
                            idMax = clients.get(i).getId();
                        }
                    }

                    idMax++;

                    String jsonNewClient = "{"
                            + "\"id\":" + idMax + ","
                            + "\"nom\":\"" + nom + "\","
                            + "\"prenom\":\"" + prenom + "\","
                            + "\"email\":\"" + email + "\","
                            + "\"mdp\":\"" + mdp + "\","
                            + "\"age\":" + age + ","
                            + "\"telephone\":\"" + telephone + "\","
                            + "\"adresse\":\"" + adresse + "\""
                            + "}";

                    RequestBody body = RequestBody.create(jsonNewClient, MediaType.get("application/json; charset=utf-8"));

                    Request request1 = new Request.Builder().url(url).post(body).build();

                    client.newCall(request1).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            runOnUiThread(() -> Toast.makeText(Enregistrer.this, "L'ajout du client n'a pas marcher!", Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            if(response.isSuccessful()){
                                Toast.makeText(Enregistrer.this, "Ajout completer!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Enregistrer.this, Connexion.class);
                                launcher.launch(intent);
                                finish();
                            } else {
                                Toast.makeText(Enregistrer.this, "Echec de l'ajout!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}