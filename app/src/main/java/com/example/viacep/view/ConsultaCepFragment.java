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
import com.example.viacep.databinding.FragmentConsultaCepBinding;
import com.example.viacep.viacep.CepApi;
import com.example.viacep.viacep.ViaCEP;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConsultaCepFragment extends Fragment {

    private CepApi cepApi;
    private FragmentConsultaCepBinding binding;
    private int numComplement = 1;

    private RoomDB database;

    private String adress;

    String cep, bairro, logradouro, cidade, estado, pais, numero, complemento;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConsultaCepBinding.inflate(getLayoutInflater());

        invisible();
        requestFocusInputCep();

        binding.btnBuscar.setOnClickListener(v -> {
            binding.pbCep.setVisibility(View.VISIBLE);
            searchCEP();
            hideKeyboard();
            sharedPreferencesCEP();
            bottomNavigationSaveShare();
        });

        expandComplement();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String lastSearch;
        SharedPreferences preferences = getActivity().getSharedPreferences("chaveGeral", MODE_PRIVATE);
        lastSearch = preferences.getString("chaveCEP", "");
        binding.inputCep.setText(lastSearch);

        String cep = binding.inputCep.getText().toString();
        if (!cep.equals("")) {
            binding.btnBuscar.performClick();
        }
    }

    public void sharedPreferencesCEP() {
        SharedPreferences preferences = getActivity().getSharedPreferences("chaveGeral", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("chaveCEP", binding.inputCep.getText().toString());
        editor.commit();
    }

    private void searchCEP() {
        String cep = binding.inputCep.getText().toString();

        binding.cardviewBody.setVisibility(View.GONE);

        Retrofit retrofitCep = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        cepApi = retrofitCep.create(CepApi.class);

        cepApi.consultarCEP(cep).enqueue(new Callback<ViaCEP>() {
            @Override
            public void onResponse(Call<ViaCEP> call, Response<ViaCEP> response) {
                validateEmpty();

                if (response.isSuccessful()) {
                    try {
                        ViaCEP cep = response.body();

                        binding.cardviewBody.setVisibility(View.VISIBLE);
                        binding.pbCep.setVisibility(View.GONE);

                        binding.tvGetNumeroCep.setText(cep.getCep());
                        binding.tvGetBairro.setText(cep.getBairro());
                        binding.tvGetLogradouro.setText(cep.getLogradouro());
                        binding.tvGetCidade.setText(cep.getLocalidade());
                        binding.tvGetEstado.setText(cep.getUf());
                        binding.tvGetPais.setText(" - Brasil");

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Verifique se o CEP foi preenchido corretamente!", Toast.LENGTH_SHORT).show();
                        binding.pbCep.setVisibility(View.GONE);
                        binding.cardviewBody.setVisibility(View.GONE);
                    }

                } else {
                    binding.pbCep.setVisibility(View.GONE);
                    binding.cardviewBody.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ViaCEP> call, Throwable t) {
                validateEmpty();
                Toast.makeText(getContext(), "Erro ao buscar o CEP!", Toast.LENGTH_SHORT).show();
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
        String cep = binding.inputCep.getText().toString();
        String inputSearchCep = cep.replaceAll("-", "").replaceAll("_", "");

        if (inputSearchCep.length() <=7) {
            binding.inputCep.setError("Insira um número de CEP válido!");
            binding.inputCep.requestFocus();
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

                        //abrir teclado automaticamente quando executa requestFocusInputCep no input
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
            Toast.makeText(getContext(), "Endereço salvo em sua lista!", Toast.LENGTH_LONG).show();
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

        new Handler().postDelayed(inputPhone::requestFocus, 900);

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

            cep = binding.inputCep.getText().toString().trim();
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
        cep = binding.inputCep.getText().toString().trim();
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

    private void requestFocusInputCep() {
        new Handler().postDelayed(() -> {
            binding.inputCep.requestFocus();
        }, 500);
    }
}