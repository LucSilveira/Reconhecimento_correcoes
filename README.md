# Alteracao no registro de logins do Usuario

### LoginController - modificação para registrar um novo login

No arquivo do loginController, foi preciso mudar um pouco sua estrutura para que ele sirva apenas para criar o login, mas de forma interna, sem a necessidade de uma chamada de um endpoint

```java
@Service
public class LoginController {

    @Autowired
    LoginRepository loginRepository;

    @Autowired
    UserRepository userRepository;

//    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Boolean loginComFoto(Optional<UserModel> user){

        Optional<UserModel> searchUser = userRepository.findById( user.get().getId() );

        if (searchUser.isEmpty()) {
            return false;
        }

        LoginModel loginModel = new LoginModel(searchUser.get());

        loginRepository.save( loginModel );

        return true;
    }
}
```

### PhotoController - chamando o LoginController para registro do login

Neste momento, a responsabilidade de chamar o LoginController é do PhotoController, afinal ele cuida da comunicação com o reconhecimento, e quando encontrado o usuário, passa pro loginController para registrar
o login do usuário encontrado

```java
/// ...
if (user.isPresent()){
    UserModel userLog= user.get();

    // Logando o usuario encontrado
    loginController.loginComFoto( user );


    return ResponseEntity.ok().body(userLog);
}
// ....
```

### LoginModel - Mudando as propriedades de data e aplicando o construtor

No model de login, foi preciso modificar a estrutura da data para registrar o momento exato do login, assim como a criação de um construtor para aplicar essa marcação da data. 

```java
@Getter
@Setter
@Entity
@Table(name = "tb_login")
@NoArgsConstructor
public class LoginModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_usuario" ,referencedColumnName = "id")
    private UserModel user;

    private LocalDateTime login_time;

    // Construtor personalizado para definir login_time como a data e hora atuais
    public LoginModel(UserModel user) {
        this.user = user;
        this.login_time = LocalDateTime.now();; // Definindo login_time como a data e hora atuais
    }
}
```

# Alteracao para listar os logins de um usuario

### LoginRepository - criar a busca dos logins do usuario

Neste tópico, foi preciso criar a função que faz a busca dos logins

```java
public interface LoginRepository extends JpaRepository<LoginModel, UUID> {

    List<LoginModel> findByUser(UserModel user);
}
```

### UserController - Criando método para buscar os registros de login do usuário

```java
@GetMapping("/login/{id}")
public ResponseEntity<Object> LoginByUser(@PathVariable(value = "id") UUID id)
{
    Optional<UserModel> serachUser = userRepository.findById(id);

    if (serachUser.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario nao encontrado");
    }

    List<LoginModel> loginsUser = loginRepository.findByUser( serachUser.get() );

    if (loginsUser.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    return ResponseEntity.status(HttpStatus.OK).body( loginsUser );
}
```

# Alteracao para registrar o caminho da foto do blobStorage

### UserController - Criando o modo de salvar o caminho da foto gerada

Neste momento, vamos registrar o caminho da foto gerada no blobStorage, considerando que temos que salvar essa alteração após a criação do usuario

```java
// ....
// Faz o upload do arquivo para o blob storage
var uploadBlob = Blob.UploadFileToBlob(file, fileName);

if( uploadBlob == null )
{
    // Verificando se o arquivo nao foi salvo - retornar erro
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao armazenar imagem");

}else{
    user.setFace( uploadBlob );

    // Salva o caminho da foto no banco
    BeanUtils.copyProperties(user, user);

    userRepository.save( user );
}
// ....
```

### Blob - Retornando o caminho do arquivo após a inserção

Neste tópico, vamos devolver o caminho do arquivo gerado no blob, para salvarmos essa informação no usuário

```java
// ...

// Criando uma referencia do novo arquivo que será gerado
CloudBlockBlob blob = container.getBlockBlobReference(nomeArquivo);

blob.upload(arquivo.getInputStream(), arquivo.getSize());

return blob.getUri().toString();
//...
```

# Alteracao na atualizacao da foto do usuario

### UserController - Aplicando o método de alterar somente a foto

Neste item, precisamos criar uma nova funcao para recebermos apenas a foto para ser salva no blobStorage por conta do mobile

```java
@PutMapping(value = "/photo/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
public ResponseEntity<Object> editarFotoUsuario(@PathVariable(value = "id") UUID id, @RequestParam(value = "imagem", required = false) MultipartFile imagem) {
    Optional<UserModel> searchUser = userRepository.findById(id);

    if (searchUser.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario não encontrado");
    }

    try {
        MultipartFile file = imagem;

        var uploadBlob = Blob.UpdateFileToBlob(file, id.toString());

        return ResponseEntity.status(HttpStatus.OK).body( searchUser );

    } catch (Exception e) {
        // Tratar a exceção aqui
        e.printStackTrace(); // ou qualquer outro tratamento desejado

    }

    return ResponseEntity.status(HttpStatus.CREATED).body( "Erro ao atualizar" );
}
```

### Blob - Aplicando método para salvar a alteracao da foto

Neste tópico, vamos criar o método que altera o arquivo salvo no blobStorage, devido a divergencia das extensoes do arquivo ( PNG ou JPG ), nisso buscamos a referencia do arquivo original, e alteramos apenas o conteudo da foto

```java
public static Boolean UpdateFileToBlob(MultipartFile novoArquivo, String idUsuario){
  try{
      // Acessando os recursos da conta por meio da connection string
      CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
  
      // Acessando os dados de conexao com o blob
      CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
  
      CloudBlobContainer container = blobClient.getContainerReference("containerblobstorage");
  
      // Procurando o arquivo original para alteracao
      for (ListBlobItem blobItem : container.listBlobs()){
  
          String[] path = blobItem.getUri().getPath().split("/");
          String nomeCompletoBlob = path[path.length - 1];
          String nomeBlob = nomeCompletoBlob.substring(0, nomeCompletoBlob.lastIndexOf('.'));
  
          // Verificando se o arquivo é igual
          if ( nomeBlob.equals( idUsuario )){
  
              // Criando uma referencia do novo arquivo que será gerado
              CloudBlockBlob blob = container.getBlockBlobReference( nomeCompletoBlob );
              blob.upload( novoArquivo.getInputStream(), novoArquivo.getSize() );
  
              return true;
          }
  
      }
  
  
  } catch (Exception e) {
      // Output the stack trace.
      e.printStackTrace();
  }
  
  return false;
}
```
