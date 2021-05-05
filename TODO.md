# TODO

- [x] interface do editor de texto.
- [x] Possibilidade de criar um novo ficheiro de texto.
- [] Aquando da operação de guardar o ficheiro pela primeira vez, o programa deve per-guntar se o utilizador quer
  cifrar, autenticar, ou ambas.
- [] O editor deve gerar automaticamente as chaves de cifra e de integridade e guardá-lasnum ficheiro chamado
  keys-and-iv.txt na mesma pasta onde é guardado o ficheirode texto, chamando a atenção do utilizador para o facto de
  que estão lá, e que devemser retiradas logo que terminar a edição deste ficheiro.
- [] O editor deve guardar sempre o ficheiro na forma cifrada ou autenticada no sistema.Caso a cifragem seja requerida
  pelo utilizador, a lógica aplicacional deve garantir queo ficheiro só existe em texto limpo na memória, mas nunca no
  disco.
- [] O editor deve permitir abrir um ficheiro que haja sido criado por ele, desde que oficheirokeys-and-iv.txtseja
  fornecido com aquele que se quer abrir.
- [] O editor deve ter uma opção para criação da assinatura digital de um ficheiro aberto,propondo a geração de chaves
  de cifra assimétrica se necessário.
- [] Ao abrir um ficheiro, o editor deve verificar o seuMessage Authentication Codeouassinatura digital e informar o
  utilizador do sucesso ou insucesso dessa operação.

### Extras

- [] Permitir aceder a determinada porção do ficheiro sem necessitar de o decifrar inteira-mente (i.e., trazer para a
  memória e decifrar apenas o pedaço de ficheiro que se quereditar ou visualizar).
- [] Utilizar uma infraestrutura de chave pública com certificados digitais de suporte àsfuncionalidades da assinatura
  digital (e.g., definir um certificado raíz para todos oseditores que se venham a instalar e, para cada instalação do
  programa é geradoum certificado digital, que por sua vez é usado para gerar certificados digitais paracada um dos
  utilizadores desse mesmo programa –falar com o regente da unidadecurricular acerca desta funcionalidade).
- [] Ter umhelpbastante completo e ser de simples utilização.
- []Pensem numa forma de atacar o sistema (uma falha da sua implementação) e dediquem-lhe uma secção no relatório.