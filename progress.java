public class Progresso{
 /**
     * Método void principal
     */
    private void run() {
        if (comparar())/*Se retornar verdadeiro*/ {
            verificar();/* leia o java doc desde metodo*/

        } else {//se não há o que atualizar
            new Thread()/*Nova thread para processar*/ {
                @Override
                public void run() {
                    //as linhas abaixo atualizam de forma progressiva sem copiar arquivos
                    try {
                        int x = 0;
                        barraProgresso.setMaximum(100);
                        while (x < barraProgresso.getMaximum()) {
                            barraProgresso.setValue(++x);
                            sleep(10);//espera 10milisegundos
                            textProg.setText("Carregando...");
                            if (barraProgresso.getValue() >= 99) {
                                textProg.setText("Iniciando módulo principal!");
                                sleep(200);//espera 200 milisegundos
                            }
                        }

                        new Principal(login, hora()).setVisible(true); // Continuação (aqui abre o programa subsequente)
                        dispose();
                    } catch (InterruptedException ex) {
                        JOptionPane.showMessageDialog(null, "O progresso foi interrompido \n" + ex, "Erro de atualização", JOptionPane.ERROR_MESSAGE);
                    }

                }
            }.start();
        }
    }
    
    /**
     * Método responsável por atualizar todos os arquivos do servidor para máquina local
     */
    private void verificar() {
        File subFile = new File("\\\\10.9.1.211\\update\\SCA2.0\\dist\\relatorios");
        File subFileLib = new File("\\\\10.9.1.211\\update\\SCA2.0\\dist\\lib");

        File file = new File("\\\\10.9.1.211\\update\\SCA2.0\\dist");
        int max = (file.listFiles().length + subFile.listFiles().length + subFileLib.listFiles().length);
        barraProgresso.setMaximum(max);

        new Thread() {
            @Override
            public void run() {
                Process p;
                try {
                    p = Runtime.getRuntime().exec("cmd /c XCOPY \\\\10.9.1.211\\update\\SCA2.0\\dist C:\\\"SCA 2.0\"  /I /E /Y");//comando cmd, copiar arquivo do servidor para disco C

                    String line;
                    BufferedReader stdInput = new BufferedReader(new //Buffered reader para ler as linhas do comando
                            InputStreamReader(p.getInputStream()));
                    //printa o retorno
                    int x = 0;// contador do progresso
                    while ((line = stdInput.readLine()) != null || x <= max) { // enquanto haver linha escrita ou até que o progresso chegue ao máximo de arquivos do diretório original
                        barraProgresso.setValue(++x);//aumenta progresso
                        textProg.setText(line);
                        textStatus.setText("Carregando Arquivos...");//mensagem
                        if (barraProgresso.getValue() >= barraProgresso.getMaximum()-1) {
                            textStatus.setText("Atualizando o sistema...");
                            sleep(300);
                        }
                    }
                    dispose();
                    JOptionPane.showMessageDialog(null, "O sistema foi atualizado com sucesso!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    Runtime.getRuntime().exec("cmd /c cd C:\\SCA 2.0 && java -jar SCA.jar"); //aqui poderá obter o path de execução do jar tbm
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao buscar arquivos do servidor \n" + ex, "Erro de atualização", JOptionPane.ERROR_MESSAGE);
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(null, "O progresso foi interrompido \n" + ex, "Erro de atualização", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.start();
    }
    
    /**
     * Método para compara a data de modificação do executavel do servidor com a local
     * @return      retorna true ou false
     */
    private boolean comparar() {
        
        File jarLocal = new File("C:\\SCA 2.0\\SCA.jar");
        File jarServer = new File("\\\\10.9.1.211\\update\\SCA2.0\\dist\\SCA.jar");
        return ((getLengthServer() > getLengthLocal()) || (new Date(jarServer.lastModified()).after(new Date(jarLocal.lastModified()))));
    }
    
    /**
     * Método que calcula a quantidade de arquivo existente na pasta local
     * 
     * @return    retorna quantidade de arquivos na pasta
     */
    private int getLengthLocal() {
        File local = new File("C:\\SCA 2.0");
        File localLib = new File("C:\\SCA 2.0\\lib");
        File localR = new File("C:\\SCA 2.0\\relatorios");
        return local.listFiles().length + localLib.listFiles().length + localR.listFiles().length;
    }

    /**
     * Método que calcula a quantidade de arquivo existente na pasta servidora
     * 
     * @return    retorna quantidade de arquivos na pasta
     */
    private int getLengthServer() {
        File subFile = new File("\\\\10.9.1.211\\update\\SCA2.0\\dist\\relatorios");
        File subFileLib = new File("\\\\10.9.1.211\\update\\SCA2.0\\dist\\lib");
        File file = new File("\\\\10.9.1.211\\update\\SCA2.0\\dist");
        return (file.listFiles().length + subFile.listFiles().length + subFileLib.listFiles().length);
    }
}
