package service;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class AiInsightService {
    private static final String TIDAK_TERSEDIA = "AI tidak tersedia";

    private final Properties config = new Properties();
    private final Gson gson = new Gson();

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    public AiInsightService() {
        this(Path.of("config", "ai.properties"));
    }

    // Berguna untuk pengujian dengan file konfigurasi lain.
    public AiInsightService(Path fileConfig) {
        try (Reader reader = Files.newBufferedReader(
                fileConfig, StandardCharsets.UTF_8)) {

            config.load(reader);

        } catch (IOException | IllegalArgumentException e) {
            config.clear(); // File hilang/rusak: AI dinonaktifkan.
        }
    }

    public String buatInsight(String ringkasan) {
        if (!Boolean.parseBoolean(
                config.getProperty("ai.enabled", "false").trim())) {

            return TIDAK_TERSEDIA
                    + " (dinonaktifkan atau konfigurasi belum siap).";
        }

        if (ringkasan == null || ringkasan.isBlank()) {
            return TIDAK_TERSEDIA + " (ringkasan kosong).";
        }

        String model = config.getProperty("ai.model", "").trim();

        if (model.isEmpty()) {
            return TIDAK_TERSEDIA + " (ai.model belum diisi).";
        }

        try {
            String instruksi = """
                Anda adalah asisten pembelajaran Java dan capstone anggaran.
                Jawab pertanyaan pengguna dalam bahasa Indonesia.
                Gunakan jawaban singkat, jelas dan mudah dipahami pemula.
                Jangan mengarang angka anggaran atau mengaku mengakses database.
                Jika pertanyaan membutuhkan data yang belum diberikan,
                mintalah data tersebut.
                """;

            Map<String, Object> payload = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of(
                                    "role", "system",
                                    "content", instruksi),
                            Map.of(
                                    "role", "user",
                                    "content", ringkasan)),
                    "temperature", 0.2,
                    "max_tokens", 1024,
                    "stream", false);

            URI endpoint = URI.create(config.getProperty(
                    "ai.endpoint",
                    "http://127.0.0.1:5001/v1/chat/completions"
            ).trim());

            HttpRequest request = HttpRequest.newBuilder(endpoint)
                    .timeout(Duration.ofSeconds(120))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            gson.toJson(payload),
                            StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(
                            StandardCharsets.UTF_8));

            if (response.statusCode() != 200) {
                return TIDAK_TERSEDIA
                        + " (HTTP " + response.statusCode() + ").";
            }

            Jawaban hasil = gson.fromJson(
                    response.body(), Jawaban.class);

            if (hasil == null
                    || hasil.choices() == null
                    || hasil.choices().isEmpty()) {

                return TIDAK_TERSEDIA + " (respons kosong).";
            }

            Pilihan pilihan = hasil.choices().get(0);

            if (pilihan == null
                    || pilihan.message() == null
                    || pilihan.message().content() == null
                    || pilihan.message().content().isBlank()) {

                return TIDAK_TERSEDIA + " (teks insight kosong).";
            }

            return pilihan.message().content().trim();

        } catch (HttpTimeoutException e) {
            return TIDAK_TERSEDIA + " (waktu tunggu habis).";

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return TIDAK_TERSEDIA + " (permintaan dibatalkan).";

        } catch (IOException e) {
            return TIDAK_TERSEDIA
                    + " (KoboldCpp tidak dapat dihubungi).";

        } catch (JsonParseException | IllegalArgumentException e) {
            return TIDAK_TERSEDIA
                    + " (konfigurasi atau respons JSON tidak valid).";
        }
    }

    // Struktur JSON: choices[0].message.content
    private record Jawaban(List<Pilihan> choices) { }

    private record Pilihan(Pesan message) { }

    private record Pesan(String content) { }
}