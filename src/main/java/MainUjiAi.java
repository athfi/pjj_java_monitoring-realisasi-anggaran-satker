import java.util.Scanner;
import service.AiInsightService;

public class MainUjiAi {
    public static void main(String[] args) {
        AiInsightService ai = new AiInsightService();

        System.out.println("=== AI Lokal KoboldCpp ===");
        System.out.println("Ketik pertanyaan, atau 'keluar' untuk berhenti.");

        try (Scanner input = new Scanner(System.in)) {
            while (true) {
                System.out.print("\nAnda: ");

                if (!input.hasNextLine()) {
                    break;
                }

                String pertanyaan = input.nextLine().trim();

                if (pertanyaan.equalsIgnoreCase("keluar")) {
                    break;
                }

                if (pertanyaan.isBlank()) {
                    continue;
                }

                System.out.println("AI sedang menjawab...");
                System.out.println("\nAI: " + ai.buatInsight(pertanyaan));
            }
        }
    }
}