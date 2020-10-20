
import com.jcraft.jsch.*;

import java.io.*;

public class Main {
    public static void main(String[] args) throws JSchException {
//        Main.SimpleConnect();
        Main.redirectConnect();
    }

    public static void SimpleConnect(){
        JSch jSch = new JSch();
        try {
            Session session = jSch.getSession("test", "127.0.0.1", 2222);
            session.setPassword("toor");
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            Channel exec = session.openChannel("shell");

            exec.setInputStream(System.in, true);
            exec.setOutputStream(System.out);

            exec.connect();
        } catch (JSchException e) {
            e.printStackTrace();
        }
    }

    public static void redirectConnect(){
        JSch jSch = new JSch();
        try {
            Session session = jSch.getSession("test", "127.0.0.1", 2222);
            session.setPassword("toor");
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            Channel exec = session.openChannel("shell");

            PipedOutputStream pipe = new PipedOutputStream();
            PipedInputStream in = new PipedInputStream(pipe);
            PrintWriter pw = new PrintWriter(pipe);

            exec.setInputStream(in, true);

            PrintStream out = System.out;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            System.setOut(ps);
            exec.setOutputStream(out);

            String[] cmds = {"cd /", "ls", "pwd"};
            for (String cmd : cmds) {
                pw.println(cmd);
            }
            pw.println("exit");
            pw.flush();

            exec.connect();

            while (!exec.isClosed()) {
            }
            System.setOut(out);
            System.out.println(baos.toString());

            exec.disconnect();
            session.disconnect();
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
    }
}
