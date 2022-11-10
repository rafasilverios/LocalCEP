package com.example.viacep.viacep;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CepApi {

    @GET("{cep}/json/")
    Call<ViaCEP> consultarCEP(@Path("cep") String cep);

    @GET("{uf}/{localidade}/{logradouro}/json/")
    Call<List<ViaCEP>> consultarEndereco(@Path("uf") String uf,@Path("localidade") String localidade, @Path("logradouro") String logradouro);
}
