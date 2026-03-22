import javax.sound.sampled.*;

public class SoundManager {

    private boolean muted = false;

    public void toggleMute() { muted = !muted; }
    public boolean isMuted() { return muted; }

    public void playEat() {
        play(() -> generateTone(880, 60, 0.18f, WaveShape.SQUARE));
    }


    public void playSpecialEat() {
        play(() -> {
            byte[] a = generateTone(1047, 60,  0.2f,  WaveShape.SQUARE);
            byte[] b = generateTone(1319, 80,  0.2f,  WaveShape.SQUARE);
            return concat(a, b);
        });
    }


    public void playDeath() {
        play(() -> {
            byte[] a = generateTone(220, 120, 0.3f, WaveShape.SAWTOOTH);
            byte[] b = generateTone(150, 200, 0.3f, WaveShape.SAWTOOTH);
            byte[] c = generateTone(80,  250, 0.25f, WaveShape.SAWTOOTH);
            return concat(a, b, c);
        });
    }

    public void playStart() {
        play(() -> {
            byte[] a = generateTone(523, 80, 0.15f, WaveShape.SQUARE);
            byte[] sil = silence(30);
            byte[] b = generateTone(659, 100, 0.15f, WaveShape.SQUARE);
            return concat(a, sil, b);
        });
    }

    public void playPause() {
        play(() -> generateTone(330, 50, 0.12f, WaveShape.SQUARE));
    }


    private enum WaveShape { SQUARE, SAWTOOTH }

    private interface PcmSupplier { byte[] get(); }

    private void play(PcmSupplier supplier) {
        if (muted) return;
        Thread t = new Thread(() -> {
            try {
                byte[] pcm = supplier.get();
                AudioFormat fmt = new AudioFormat(44100, 8, 1, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, fmt);
                if (!AudioSystem.isLineSupported(info)) return;

                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(fmt);
                line.start();
                line.write(pcm, 0, pcm.length);
                line.drain();
                line.close();
            } catch (Exception ignored) {}
        });
        t.setDaemon(true);
        t.start();
    }

    /**
     * 
     *
     * @param freq
     * @param durationMs
     * @param volume
     * @param shape
     */
    private byte[] generateTone(int freq, int durationMs, float volume, WaveShape shape) {
        int samples = (int)(44100 * durationMs / 1000.0);
        byte[] buf  = new byte[samples];
        double period = 44100.0 / freq;

        for (int i = 0; i < samples; i++) {
            double pos = (i % period) / period;

            double raw = switch (shape) {
                case SQUARE   -> pos < 0.5 ? 1.0 : -1.0;
                case SAWTOOTH -> 1.0 - 2.0 * pos;
            };

            float envelope = (i > samples * 0.8f)
                    ? (float)(samples - i) / (samples * 0.2f)
                    : 1f;

            buf[i] = (byte)(raw * volume * envelope * 127);
        }
        return buf;
    }

    private byte[] silence(int durationMs) {
        return new byte[(int)(44100 * durationMs / 1000.0)];
    }

    private byte[] concat(byte[]... arrays) {
        int total = 0;
        for (byte[] a : arrays) total += a.length;
        byte[] result = new byte[total];
        int pos = 0;
        for (byte[] a : arrays) {
            System.arraycopy(a, 0, result, pos, a.length);
            pos += a.length;
        }
        return result;
    }
}
