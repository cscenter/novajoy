package novajoy.util.archiver;

import novajoy.util.logger.Loggers;

import java.io.*;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * User: romanfilippov
 * Date: 28.04.13
 * Time: 12:32
 */
public class Archiver {

    private static Archiver archiver = null;

    private static Logger log =  new Loggers().getPackerLogger();

    public Archiver getArchiver() {
        if (archiver == null) {
            archiver = new Archiver();
        }
        return archiver;
    }

    public static boolean createZip(String dirName, String zipName) {
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipName));
            //zos.setLevel(Deflater.DEFLATED);

            //TBD Add format choosing variants
            zipEpub(dirName, zos, dirName);

            zos.close();

            File f = new File(zipName);
            if (f.exists()){
                return true;
            }
            else{
                return false;
            }
        } catch (Exception e) {
            log.warning("Archiving failed. cause: " + e.getMessage());
            return false;
        }
    }

    private static String readFile(String filename)
    {
        if (filename == null)
            return null;
        String content = null;
        File file = new File(filename);
        try {
            FileReader reader = new FileReader(file);
            char[] chars = new char[(int) file.length()];
            reader.read(chars);
            content = new String(chars);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private static void zipEpub(String dir2zip, ZipOutputStream zos, String True_path) {
        try {

            File zipDirs = new File(dir2zip);

            String[] dirList = zipDirs.list();
            byte[] readBuffer = new byte[2048];
            int bytesIn = 0;

            for (int i = 0; i < dirList.length; i++) {
                File f = new File(zipDirs, dirList[i]);
                //System.out.println(f.getAbsolutePath());
                if (f.isDirectory()) {

                    String filePath = f.getPath();
                    zipEpub(filePath, zos, True_path);
                    continue;
                }

                InputStream fis = new FileInputStream(f);
                // Создадим объект zip архива с именем упаковываемой папки или файла
                String entryPath = f.getPath().substring(True_path.length()+1);
                ZipEntry anEntry = new ZipEntry(entryPath);
                // place the zip entry in the ZipOutputStream object
                if (f.getName().contains(".DS_Store"))
                    continue;

                if (f.getName().equalsIgnoreCase("mimetype")) {

                    anEntry.setMethod(ZipOutputStream.STORED);
                    anEntry.setSize(f.length());
                    anEntry.setCompressedSize(f.length());

                    String fileContent = readFile(f.getPath());
                    CRC32 crc = new CRC32();
                    crc.update(fileContent.getBytes());
                    anEntry.setCrc(crc.getValue());

                    zos.putNextEntry(anEntry);
                    // now write the content of the file to the ZipOutputStream

                    byte[] arr = fileContent.getBytes();
                    zos.write(arr);

                }
                else {
                    zos.setLevel(Deflater.BEST_COMPRESSION);

                    zos.putNextEntry(anEntry);
                    while ((bytesIn = fis.read(readBuffer)) != -1) {

                        zos.write(readBuffer, 0, bytesIn);
                    }
                }

                fis.close();

            }
        } catch (Exception e) {
            log.warning("File archiving failed: " + e.getMessage());
        }
    }

    public static String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        String result = "";

        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }

    public static byte[] createChecksum(String filename) throws Exception {
        InputStream fis =  new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }
}
