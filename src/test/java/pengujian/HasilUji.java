package pengujian;

import java.util.Optional;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

/** Menampilkan hasil sesungguhnya; assertion dan status Maven tetap menentukan kelulusan. */
public class HasilUji implements TestWatcher {
    private void tampil(String status, ExtensionContext context) {
        String nama = context.getRequiredTestMethod().getName();
        String detail = context.getDisplayName();
        if (!detail.equals(nama + "()")) nama += " " + detail;
        System.out.println(status + " " + nama);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        tampil("\u2713 [PASS]", context);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        tampil("\u2717 [FAIL]", context);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        tampil("! [ABORT]", context);
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        tampil("- [SKIP]", context);
    }
}
