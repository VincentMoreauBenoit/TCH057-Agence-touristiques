package com.ets.projet2025.agence_touristique;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class Connexion extends AppCompatActivity {

    private OkHttpClient client = new OkHttpClient();
    private ObjectMapper objectMapper = new ObjectMapper();
    private EditText emailText, mdpText;
    private Button connexion;
    private Button enregistrer;
    private ActivityResultLauncher<Intent> launcher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailText = findViewById(R.id.textConnexionEmail);
        mdpText = findViewById(R.id.textMDPConnexion);
        connexion = findViewById(R.id.btnConnexion);
        enregistrer = findViewById(R.id.btnEnregistrer);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Toast.makeText(Connexion.this, "Retour réussi", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        connexion.setOnClickListener(view -> {
            String email = emailText.getText().toString();
            String mdp = mdpText.getText().toString();

            regarderConnexion(email, mdp);
        });

        enregistrer.setOnClickListener(view -> {
            Intent intent = new Intent(Connexion.this, Enregistrer.class );
            launcher.launch(intent);
            finish();
        });

    }

    private void regarderConnexion(String email, String mdp){
        String url = "http://10.0.2.2:3000/clients?email=" + email + "&mdp=" + mdp;

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Connexion.this, "Erreur de conexion", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String reponse = response.body().string();
                    List<Client> clients = objectMapper.readValue(reponse, new TypeReference<List<Client>>(){});

                    if(clients != null && !clients.isEmpty()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Connexion.this, "Connexion réussi", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Connexion.this, PagePrincipaleActivity.class);
                                launcher.launch(intent);
                                finish(); // A voir si important
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Connexion.this, "Indentifiants inccorecte", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }
}