package com.example.acer.voice.signal_processing;

import java.io.*;
import javax.sound.sampled.*;


public class AudioSampleWriter implements Runnable {

    File file;
    AudioFormat format;
    AudioFileFormat.Type targetType;

    PipedOutputStream pos;
    PipedInputStream pis;
    AudioInputStream ais;
    byte[] bytes;

    public AudioSampleWriter(File file, AudioFormat format,
                             AudioFileFormat.Type targetType) throws IOException {
        this.format = format;
        this.targetType = targetType;
        this.file = file;

        // Write to the output stream
        pos = new PipedOutputStream();

        // It will then go to the file via the input streams
        pis = new PipedInputStream(pos);
        ais = new AudioInputStream(pis, format, AudioSystem.NOT_SPECIFIED);

        new Thread(this).start();
    }

    public void run() {
        try {
            AudioSystem.write(ais, targetType, file);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void write(double[] interleavedSamples) throws IOException {
        writeInterleavedSamples(interleavedSamples, interleavedSamples.length);
    }

    public void writeInterleavedSamples(double[] interleavedSamples,
                                        int sampleCount) throws IOException {
        // Allocate a new bytes array if necessary. If bytes is too long,
        // don't worry about it, just use as much as is needed.
        int numBytes = sampleCount * (format.getSampleSizeInBits() / 8);
        System.out.println("numBytesOut=" + numBytes);
        if (bytes == null || numBytes > bytes.length)
            bytes = new byte[numBytes];

        // Convert doubles to bytes using format
        encodeSamples(interleavedSamples, bytes, sampleCount);

        // write it
        pos.write(bytes, 0, numBytes);
    }

    public void close() throws IOException {
        if (pos != null) {
            ais.close();
            pis.close();
            pos.close();
        }
    }


    private void encodeSamples(double[] audioData, byte[] audioBytes,
                               int length) {
        int in;
        if (format.getSampleSizeInBits() == 16) {
            if (format.isBigEndian()) {
                for (int i = 0; i < length; i++) {
                    in = (int)(audioData[i]*32767);
                    /* First byte is MSB (high order) */
                    audioBytes[2*i] = (byte)(in >> 8);
                    /* Second byte is LSB (low order) */
                    audioBytes[2*i+1] = (byte)(in & 255);
                }
            } else {
                for (int i = 0; i < length; i++) {
                    in = (int)(audioData[i]*32767);
                    /* First byte is LSB (low order) */
                    audioBytes[2*i] = (byte)(in & 255);
                    /* Second byte is MSB (high order) */
                    audioBytes[2*i+1] = (byte)(in >> 8);
                }
            }
        } else if (format.getSampleSizeInBits() == 8) {
            if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
                for (int i = 0; i < length; i++) {
                    audioBytes[i] = (byte)(audioData[i]*127);
                }
            } else {
                for (int i = 0; i < length; i++) {
                    audioBytes[i] = (byte)(audioData[i]*127 + 127);
                }
            }
        }
    }

//    public static void main(String[] args) {
//        try {
//            File a = new File("open_ground_2_mic.wav");
//            AudioSampleReader sampleReader = new AudioSampleReader(a);
//            AudioFormat format = sampleReader.getFormat();
//            long nbSamples = sampleReader.getSampleCount();
//            System.out.println("nbChannel=" + format.getChannels());
//            System.out.println("frameRate=" + format.getFrameRate());
//            System.out.println("sampleSize=" + format.getSampleSizeInBits());
//            double[] samples = new double[2*(int)nbSamples];
//            sampleReader.getInterleavedSamples(0, nbSamples, samples);
//            double[] samples1 = new double[(int)nbSamples];
//            samples1 = sampleReader.getChannelSamples(1,samples,samples1);
//            // lowers sound level
//            for (int i = 0; i < samples.length; i++) {
//                samples[i] /= 4.;
//
//            }
//
//            System.out.println(sampleReader.getSampleCount());
//            File outFile = new File("a.wav");
//            AudioWriter audioWriter = new AudioWriter(outFile, sampleReader.getFormat(), AudioFileFormat.Type.WAVE);
//            audioWriter.write(samples, samples.length);
//            audioWriter.close();
//            double max = samples1[0];
//            for(int j =0; j<sampleReader.getSampleCount();j++){
//                //System.out.println(samples1[j]);
//                if(samples1[j]>=max)
//                    max = samples1[j];
//
//            }
//            System.out.println();
//            System.out.println(sampleReader.getSampleCount());
//            System.out.println(max);
//
//        } catch (UnsupportedAudioFileException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


}