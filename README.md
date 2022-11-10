# LocalCEP
APP criado com conhecimento que fui adquirindo ao longo do Estágio em Desenvolvimento Android Java na Foursys.

- Consumindo API ViaCEP com Retrofit, é possível realizar consultas de CEP preenchendo campos como logradouro, cidade e estado e, também é possível consultar um endereço através do número de um CEP.

- Todos os campos preenchidos são salvos com os ultimos dados, utilizando SharedPreferences, para possibilitar a navegação no APP através de uma Bottom Navgation (biblioteca MeowBottomNavigation), sem haja perda de informações ao realizar troca de tela(fragments). Desta forma, o usuário pode navegar no app sem que seja obrigado a digitar  o usuário a preencher campos toda vez que mudar de tela.
Uma vez que o App é finalizado, os dados são apagados, através de um método na MainActivity. Assim o usuário navega no app sem perder os dados e quando o abre novamente, os campos estão limpos para novas pesquisas.

- ...
