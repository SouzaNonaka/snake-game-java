Snake Game (Java)

Um jogo da cobrinha feito em Java. Começou como um projeto simples de estudo e acabou evoluindo bastante conforme fui melhorando o código e adicionando novas ideias.

Sobre o projeto-

Criei esse projeto com o intuito de praticar orientação à objeto e para criar um jogo meu, mesmo que pequeno e simples.
Foi meio que um exercício de pegar algo simples e ir evoluindo aos poucos.

A ideia principal aqui foi botar em prática:

orientação a objetos
organização de código
como evoluir um projeto simples sem virar bagunça
e também melhorar um pouco o visual e a experiência
Funcionalidades
Sistema de estados (menu, jogando, pausa e game over)
Score em tempo real
High score salvo automaticamente
Comida especial que aparece de tempos em tempos
A velocidade aumenta conforme você joga
Controles por teclado e mouse
Sons gerados por código
Interface com HUD e telas básicas
Estrutura do projeto

Separei o código em várias classes pra não ficar tudo dentro de uma só:

GameConfig → constantes do jogo (tamanho, cores, etc)
GameState → estados do jogo (menu, playing, pause…)
SnakeSegment → representa cada parte da cobra
Snake → lógica da cobra (movimento, colisão, crescimento)
Food → controla a comida normal e especial
ScoreManager → cuida do score e do recorde
Renderer → tudo relacionado a desenho na tela
InputHandler → entrada do teclado e mouse
SoundManager → sons gerados via código
SnakeGame → classe principal que junta tudo
Controles
Setas ou WASD → mover
Enter / Espaço / clique → iniciar ou reiniciar
P ou ESC → pausar
M → ligar/desligar som
Tecnologias usadas
Java (Swing / AWT)
Programação orientada a objetos
Renderização 2D
API de Preferences (pra salvar o high score)
Objetivo

Eu ainda pretendo criar melhorias visuais, dar mais efeitos, um menu mais completo e pensando ainda em modo ou mais níveis
