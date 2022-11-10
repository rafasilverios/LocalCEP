# LocalCEP
APP criado com conhecimento que fui adquirindo ao longo do Estágio em Desenvolvimento Android Java na Foursys.

- Consumindo API ViaCEP com Retrofit, é possível realizar consultas de CEP preenchendo campos como logradouro, cidade e estado e, também é possível consultar um endereço através do número de um CEP.

- Todos os campos preenchidos são salvos com os ultimos dados, utilizando SharedPreferences, para possibilitar a navegação no APP através de uma Bottom Navgation (biblioteca MeowBottomNavigation), sem haja perda da ultima pesquisa quando o usuário realizar troca de tela(fragments). Desta forma, o usuário pode navegar no app e ao retornar à tela onde realizou a .
Uma vez que o App é finalizado, os dados são apagados, através de um método na MainActivity. Assim o usuário navega no app sem perder os dados e quando o abre novamente, os campos estão limpos para novas pesquisas.

- ...
