package com.example.viacep.view;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.viacep.db.MainData;
import com.example.viacep.R;
import com.example.viacep.db.RoomDB;
import com.example.viacep.databinding.FragmentConsultaEnderecoBinding;
import com.example.viacep.viacep.CepApi;
import com.example.viacep.viacep.ViaCEP;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConsultaEnderecoFragment extends Fragment {

    private CepApi cepApi;
    private FragmentConsultaEnderecoBinding binding;
    private int numComplement = 1;

    private RoomDB database;

    private String adress;

    String cep, bairro, logradouro, cidade, estado, pais, numero, complemento;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConsultaEnderecoBinding.inflate(getLayoutInflater());

        invisible();
        requestFocus();

        binding.btnBuscar.setOnClickListener(v -> {
            binding.pbCep.setVisibility(View.VISIBLE);
            searchAdress();
            hideKeyboard();
            sharedPreferencesCEP();
            bottomNavigationSaveShare();
        });

        expandComplement();
        copyCEP();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String lastSearchLog;
        String lastSearchCid;
        String lastSearchUf;
        SharedPreferences preferences = getActivity().getSharedPreferences("chaveGeral", MODE_PRIVATE);
        lastSearchLog = preferences.getString("chaveLogradouro", "");
        binding.inputLogradouro.setText(lastSearchLog);
        lastSearchCid = preferences.getString("chaveCidade", "");
        binding.inputCidade.setText(lastSearchCid);
        lastSearchUf = preferences.getString("chaveEstado", "");
        binding.inputUf.setText(lastSearchUf);

        String uf = binding.inputUf.getText().toString();
        String localidade = binding.inputCidade.getText().toString();
        String logradouro = binding.inputLogradouro.getText().toString();

        if (!uf.equals("") && !localidade.equals("") && !logradouro.equals("")) {
            binding.btnBuscar.performClick();
        }
    }

    public void sharedPreferencesCEP() {
        SharedPreferences preferences = getActivity().getSharedPreferences("chaveGeral", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("chaveLogradouro", binding.inputLogradouro.getText().toString());
        editor.putString("chaveCidade", binding.inputCidade.getText().toString());
        editor.putString("chaveEstado", binding.inputUf.getText().toString());
        editor.commit();
    }

    private void searchAdress() {
        String uf = binding.inputUf.getText().toString();
        String localidade = binding.inputCidade.getText().toString();

        binding.cardviewBody.setVisibility(View.GONE);

        String textInPut = binding.inputLogradouro.getText().toString();
        String newText = textInPut.replace(" ", "+");
        String logradouro = newText;

        Retrofit retrofitCep = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        cepApi = retrofitCep.create(CepApi.class);

        cepApi.consultarEndereco(uf, localidade, logradouro).enqueue(new Callback<List<ViaCEP>>() {
            @Override
            public void onResponse(Call<List<ViaCEP>> call, Response<List<ViaCEP>> response) {

                validateEmpty();

                if (response.isSuccessful()) {
                    try {
                        binding.cardviewBody.setVisibility(View.VISIBLE);
                        binding.pbCep.setVisibility(View.GONE);

                        binding.tvGetNumeroCep.setText(response.body().get(0).getCep());
                        binding.tvGetBairro.setText(response.body().get(0).getBairro());
                        binding.tvGetLogradouro.setText(response.body().get(0).getLogradouro());
                        binding.tvGetCidade.setText(response.body().get(0).getLocalidade());
                        binding.tvGetEstado.setText(response.body().get(0).getUf());
                        binding.tvGetPais.setText(" - Brasil");

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Verifique se os campos foram preenchidos corretamente!", Toast.LENGTH_SHORT).show();
                        binding.pbCep.setVisibility(View.GONE);
                        binding.cardviewBody.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(getContext(), "Verifique se os campos foram preenchidos corretamente!", Toast.LENGTH_SHORT).show();
                    binding.pbCep.setVisibility(View.GONE);
                    binding.cardviewBody.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<ViaCEP>> call, Throwable t) {
                validateEmpty();
                Toast.makeText(getContext(), "Erro ao buscar o Endereço!", Toast.LENGTH_SHORT).show();
                binding.pbCep.setVisibility(View.GONE);
                binding.cardviewBody.setVisibility(View.GONE);
            }
        });
    }

    public void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getLayoutInflater().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void copyCEP() {
        binding.ivCopyCEP.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getLayoutInflater().getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("EditText", binding.tvGetNumeroCep.getText().toString());
            clipboardManager.setPrimaryClip(clip);

            Toast.makeText(getContext(), "Endereço Copiado para a área de transferência!", Toast.LENGTH_SHORT).show();
        });
    }

    public void copyAdress() {
        ClipboardManager clipboardManager = (ClipboardManager) getLayoutInflater().getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("EditText", binding.tvGetLogradouro.getText().toString() + ", " + binding.tvGetBairro.getText().toString()
                + ", " + binding.tvGetCidade.getText().toString() + "/" + binding.tvGetEstado.getText().toString() + binding.tvGetPais.getText().toString()
                + " - CEP " + binding.tvGetNumeroCep.getText().toString());
        clipboardManager.setPrimaryClip(clip);

        Toast.makeText(getContext(), "Endereço Copiado para a área de transferência!", Toast.LENGTH_SHORT).show();
    }

    public void expandComplement() {
        binding.tvNumeroResidencial.setOnClickListener(v -> {
            if (numComplement == 1) {
                binding.icArrowDown.setRotation(180);
                binding.tiNumero.setVisibility(View.VISIBLE);
                binding.tiComplemento.setVisibility(View.VISIBLE);

                numComplement = 0;
            } else {
                binding.icArrowDown.setRotation(360);
                binding.tiNumero.setVisibility(View.GONE);
                binding.tiComplemento.setVisibility(View.GONE);

                numComplement = 1;
            }
        });

        binding.icArrowDown.setOnClickListener(v -> {
            if (numComplement == 1) {
                binding.icArrowDown.setRotation(180);
                binding.tiNumero.setVisibility(View.VISIBLE);
                binding.tiComplemento.setVisibility(View.VISIBLE);

                numComplement = 0;
            } else {
                binding.icArrowDown.setRotation(360);
                binding.tiNumero.setVisibility(View.GONE);
                binding.tiComplemento.setVisibility(View.GONE);

                numComplement = 1;
            }
        });
    }

    public void validateEmpty() {
        String uf = binding.inputUf.getText().toString();
        String localidade = binding.inputCidade.getText().toString();
        String logradouro = binding.inputLogradouro.getText().toString();

        if (logradouro.length() < 3) {
            binding.inputLogradouro.setError("Insira o nome do Logradouro que está procurando!");
            binding.inputLogradouro.requestFocus();
            binding.pbCep.setVisibility(View.GONE);
            binding.cardviewBody.setVisibility(View.GONE);
            return;
        }

        if (localidade.length() < 3) {
            binding.inputCidade.setError("Insira a Cidade que está procurando!");
            binding.inputCidade.requestFocus();
            binding.pbCep.setVisibility(View.GONE);
            binding.cardviewBody.setVisibility(View.GONE);
            return;
        }

        if (uf.length() < 2) {
            binding.inputUf.setError("Insira a sigla do Estado que está procurando!");
            binding.inputUf.requestFocus();
            binding.pbCep.setVisibility(View.GONE);
            binding.cardviewBody.setVisibility(View.GONE);
            return;
        }
    }

    public void bottomNavigationSaveShare() {
        database = RoomDB.getInstance(getContext());

        binding.bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_bottom_save:
                        ifNumberComplement();
                        saveDataList();
                        hideKeyboard();
                        break;
                    case R.id.item_bottom_share:
                        dialogShareAdress();
                        copyAdress();

                        //abrir teclado automaticamente quando executa requestFocus no input
                        /*InputMethodManager imm = (InputMethodManager) getLayoutInflater().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(bindingDialog.inputCelularDialog, InputMethodManager.SHOW_IMPLICIT);*/
                        break;
                }
                return false;
            }
        });
    }

    private void saveDataList() {
        if (!adress.equals("")) {
            try {
                MainData data = new MainData();
                data.setText(adress);
                database.mainDao().insert(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(getContext(), "Endereço salvo em sua lista!", Toast.LENGTH_SHORT).show();
        }
    }

    private void dialogShareAdress() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.activity_dialog_share);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ImageView icClose = dialog.findViewById(R.id.ic_close);
        ImageView btnWhatsApp = dialog.findViewById(R.id.iv_whatsapp);
        Button btnContatoDialog = dialog.findViewById(R.id.btn_contato_dialog);
        EditText inputPhone = dialog.findViewById(R.id.input_celular_dialog);

        inputPhone.requestFocus();

        btnContatoDialog.setOnClickListener(v -> {
            ifNumberComplement();

            String url;
            url = "https://wa.me/?text=" + adress + "\n\nEste%20endereço%20foi%20gerado%20pelo%20App%20*LocalCEP*";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

            dialog.dismiss();
        });

        btnWhatsApp.setOnClickListener(v -> {
            String phone = inputPhone.getText().toString().trim();
            String newText = phone.replace("+", "").replaceAll("_", "").replace("(", "")
                    .replace(")", "").replaceAll(" ", "").replace("-", "");
            String telefone = newText;

            cep = binding.tvGetNumeroCep.getText().toString().trim();
            bairro = binding.tvGetBairro.getText().toString().trim();
            logradouro = binding.tvGetLogradouro.getText().toString().trim();
            cidade = binding.tvGetCidade.getText().toString().trim();
            estado = binding.tvGetEstado.getText().toString().trim();
            pais = binding.tvGetPais.getText().toString().trim();
            numero = binding.inputNumero.getText().toString().trim();
            complemento = binding.inputComplemento.getText().toString().trim();

            if (telefone.isEmpty()) {
                inputPhone.setError("Insira um número de telefone com DDD!");
                inputPhone.requestFocus();
                return;
            } else if (telefone.length() < 13) {
                Toast.makeText(getContext(), telefone, Toast.LENGTH_SHORT).show();
                inputPhone.setError("Insira um número completo com \nDDD. Ex.: (11) 99999-9999");
                inputPhone.requestFocus();
                return;
            } else {
                String url;
                if (numero.isEmpty() && complemento.isEmpty()) {
                    url = "https://wa.me/" + telefone + "?text=*Endereço%3A* " + logradouro + ",%20" +
                            bairro + ",%20" + cidade + "%2F" + estado + "%20" + pais + "%20-%20CEP%20" +
                            cep + "\n\nEste%20endereço%20foi%20gerado%20pelo%20App%20*LocalCEP*";
                } else if (numero.isEmpty()) {
                    url = "https://wa.me/" + telefone + "?text=*Endereço%3A* " + logradouro + ",%20" +
                            complemento + ",%20" + bairro + ",%20" + cidade + "%2F" + estado + "%20" +
                            pais + "%20-%20CEP%20" + cep + "\n\nEste%20endereço%20foi%20gerado%20pelo%20App%20*LocalCEP*";
                } else if (complemento.isEmpty()) {
                    url = "https://wa.me/" + telefone + "?text=*Endereço%3A* " + logradouro + ",%20" +
                            numero + ",%20" + bairro + ",%20" + cidade + "%2F" + estado + "%20" + pais +
                            "%20-%20CEP%20" + cep + "\n\nEste%20endereço%20foi%20gerado%20pelo%20App%20*LocalCEP*";
                } else {
                    url = "https://wa.me/" + telefone + "?text=*Endereço%3A* " + logradouro + ",%20" + numero +
                            ",%20" + complemento + ",%20" + bairro + ",%20" + cidade + "%2F" + estado + "%20"
                            + pais + "%20-%20CEP%20" + cep + "\n\nEste%20endereço%20foi%20gerado%20pelo%20App%20*LocalCEP*";
                }

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
            dialog.dismiss();
        });

        icClose.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    public void invisible() {
        binding.cardviewBody.setVisibility(View.GONE);
        binding.tiNumero.setVisibility(View.GONE);
        binding.tiComplemento.setVisibility(View.GONE);
        binding.pbCep.setVisibility(View.GONE);
    }

    public void ifNumberComplement() {
        cep = binding.tvGetNumeroCep.getText().toString().trim();
        bairro = binding.tvGetBairro.getText().toString().trim();
        logradouro = binding.tvGetLogradouro.getText().toString().trim();
        cidade = binding.tvGetCidade.getText().toString().trim();
        estado = binding.tvGetEstado.getText().toString().trim();
        pais = binding.tvGetPais.getText().toString().trim();
        numero = binding.inputNumero.getText().toString().trim();
        complemento = binding.inputComplemento.getText().toString().trim();

        if (numero.isEmpty() && complemento.isEmpty()) {
            adress = logradouro + ", " + bairro + ", " + cidade + "/" + estado + " " + pais + " - CEP " + cep;
        } else if (numero.isEmpty()) {
            adress = logradouro + ", " + complemento + ", " + bairro + ", " + cidade + "/" + estado + " " + pais + " - CEP " + cep;
        } else if (complemento.isEmpty()) {
            adress = logradouro + ", " + numero + ", " + bairro + ", " + cidade + "/" + estado + " " + pais + " - CEP " + cep;
        } else {
            adress = logradouro + ", " + numero + ", " + complemento + ", " + bairro + ", " + cidade + "/" + estado + " " + pais + " - CEP " + cep;
        }
    }

    private void requestFocus() {
        new Handler().postDelayed(() -> {
            binding.inputLogradouro.requestFocus();
        }, 500);
    }
}