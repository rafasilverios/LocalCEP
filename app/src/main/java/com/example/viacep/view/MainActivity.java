package com.example.viacep.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.viacep.R;
import com.example.viacep.databinding.ActivityMainBinding;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSharedPreferencesNull();

        binding.bnvMain.add(new MeowBottomNavigation.Model(1, R.drawable.ic_busca_cep));
        binding.bnvMain.add(new MeowBottomNavigation.Model(2, R.drawable.ic_endereco));
        binding.bnvMain.add(new MeowBottomNavigation.Model(3, R.drawable.ic_list));

        binding.bnvMain.show(2, true);
        replace(new ConsultaEnderecoFragment());
        binding.bnvMain.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()) {
                    case 1:
                        replace(new ConsultaCepFragment());
                        break;
                    case 2:
                        replace(new ConsultaEnderecoFragment());
                        break;
                    case 3:
                        replace(new ListaFragment());
                        break;
                }
                return null;
            }
        });

    }

    public void setSharedPreferencesNull() {
        SharedPreferences preferences = getSharedPreferences("chaveGeral", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("chaveCEP", "");
        editor.putString("chaveLogradouro", "");
        editor.putString("chaveCidade", "");
        editor.putString("chaveEstado", "");
        editor.commit();
    }

    private void replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_main, fragment);
        transaction.commit();
    }
}