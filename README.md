# LocalCEP
APP criado com conhecimento que fui adquirindo ao longo do Estágio em Desenvolvimento Android Java na Foursys.

- Consumindo API ViaCEP com Retrofit, é possível realizar consultas de CEP preenchendo campos como logradouro, cidade e estado.

- Também é possível consultar um endereço através do número de um CEP.

- Todos os campos preenchidos são salvos com os ultimos dados digitados, utilizando SharedPreferences, para assim, possibilitar a navegação no APP através de uma Bottom Navgation (biblioteca MeowBottomNavigation), sem que haja perda da ultima pesquisa quando o usuário realizar troca de tela(fragments). Desta forma, o usuário pode sair e retornar à pesquisa, sem que seja obrigado a digitar os mesmos dados várias vezes.

- Uma vez que o App é finalizado(fechado), todos os dados de pesquisa são apagados, através de um método na MainActivity. Quando o usuário retornar ao app na próxima vez, poderá realizar pesquisas normalmente, sem precisar apagar os dados digitados em pesquisas anteriores e que não são mais necessários.

- Após a pesquisa, o usuário tem opção de compartilhar o endereço pelo WhatsApp de duas formas: Digitando um número específico ou clicando em um botão que direciona ao WhatsApp para que seja escolhido um numero salvo em seus contatos.

- Também há opção de salvar o endereço em uma lista do usuário. Os dados são salvos através do Room e recuperados em uma RecyclerView que também permite compartilhamento do endereço salvo e exclusão dos dados.

### Bibliotecas, linguagens e tecnologias utilizadas:
- AndroidStudio 
- Java 
- MVVM
- API
- Retrofit
- RecyclerView 
- xml
- Interface
- BottomNavigation MeowBottomNavigation
- Binding
- Mask text Santalu
- Dentre outros
