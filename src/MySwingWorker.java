import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

public class MySwingWorker extends SwingWorker<Void, String> {

    private final String filePath;
    private final JTextArea textArea;
    private final JTextField texto;
    private final int tempo;
    private final int totalLinhas;
    private final JProgressBar progressBar;
    private int lineRead = 0;

    public MySwingWorker(String filePath, JTextArea textArea, JTextField text, int tempo, JProgressBar progress) {
        this.filePath = filePath;
        this.textArea = textArea;
        this.texto = text;
        this.tempo = tempo;
        this.totalLinhas = contaTotalLinhas(filePath);
        this.progressBar = progress;
    }

    @Override
    protected Void doInBackground() throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                lineRead++;
                int progressoAtual = (int) (((double) lineRead / totalLinhas) * 100);

                publish(line); // Envia a linha lida para process() atualizar a UI

                setProgress(progressoAtual); // Atualiza a barra de progresso

                Thread.sleep(tempo); // Simula tempo de processamento
            }
        } catch (IOException e) {
            publish("Erro ao ler arquivo: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String line : chunks) {
            textArea.append(line + "\n"); // Adiciona a linha ao JTextArea
            texto.setText(line); // Atualiza o JTextField com a linha atual
        }
    }

    @Override
    protected void done() {
        try {
            textArea.append("Leitura concluída: " + filePath + "\n");
        } catch (Exception e) {
            textArea.append("Erro: " + e.getMessage() + "\n");
        }
    }

    private int contaTotalLinhas(String filePath) {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while (br.readLine() != null) {
                count++;
            }
        } catch (IOException ignored) {
        }
        return (count == 0) ? 1 : count; // Evita divisão por zero
    }
}
