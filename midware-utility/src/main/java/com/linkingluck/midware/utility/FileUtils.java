package com.linkingluck.midware.utility;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;

public class FileUtils {

	public static void clearDirectory(File targetDirectory) {
		final Stack<File> directories = new Stack<File>();
		final Stack<File> files = new Stack<File>();
		final FileFilter directoryFilter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				}
				files.push(file);
				return false;
			}
		};
		directories.push(targetDirectory);
		while (!directories.isEmpty()) {
			final File directory = directories.pop();
			files.push(directory);
			Collections.addAll(directories, directory.listFiles(directoryFilter));
		}
		for (File file : files) {
			file.delete();
		}
	}

	public static long copyFile(int cacheSize, File from, File to) throws IOException {
		final long time = new Date().getTime();

		final FileInputStream in = new FileInputStream(from);
		final FileOutputStream out = new FileOutputStream(to);
		final FileChannel inChannel = in.getChannel();
		final FileChannel outChannel = out.getChannel();

		int length;
		long result = 0;
		while (true) {
			if (inChannel.position() == inChannel.size()) {
				inChannel.close();
				outChannel.close();
				result = new Date().getTime() - time;
				break;
			}
			if ((inChannel.size() - inChannel.position()) < cacheSize) {
				length = (int) (inChannel.size() - inChannel.position());
			} else {
				length = cacheSize;
			}
			inChannel.transferTo(inChannel.position(), length, outChannel);
			inChannel.position(inChannel.position() + length);
		}
		in.close();
		out.close();
		return result;
	}

	public static void closeReader(Reader inputStream) {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public static void closeWriter(Writer outputStream) {
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public static void closeInputStream(InputStream inputStream) {
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public static void closeOutputStream(OutputStream outputStream) {
		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public static boolean createFile(File file) throws Exception {
		if (!file.exists()) {
			mkdir(file.getParentFile());
		}
		return file.createNewFile();
	}

	public static void mkdir(File file) {
		if (!file.getParentFile().exists()) {
			mkdir(file.getParentFile());
		}
		file.mkdir();
	}

	private static void doWithFile(final File file, List<File> files) throws Exception {
		if (file == null) {
			return;
		}
		if (file.isHidden()) {
			return;
		}
		if (file.isDirectory()) {
			for (File temp : file.listFiles()) {
				doWithFile(temp, files);
			}
		} else {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				String line = null;
				while ((line = br.readLine()) != null) {
					if (line.equals("@DynamicAnno")) {
						files.add(file);
						break;
					}
				}
			} finally {
				br.close();
			}
		}
	}

	private static void copyFile(File src, File des) throws Exception {
		FileChannel in = null;
		FileChannel out = null;
		try {
			in = (new FileInputStream(src)).getChannel();
			out = (new FileOutputStream(des)).getChannel();
			in.transferTo(0, in.size(), out);
		} finally {
			in.close();
			out.close();
		}
	}

	private static String getCopyFilePath(File file, File srcFile, File desFile) {
		String fp = file.getAbsolutePath();
		String srcp = srcFile.getAbsolutePath();
		String desp = desFile.getAbsolutePath();
		return desp + fp.substring(srcp.length());
	}

	public static void main(String[] args) {

		try {
			File srcDir = new File(args[0]);
			File desDir = new File(args[1]);

			List<File> files = new ArrayList<File>(10);

			doWithFile(srcDir, files);

			for (File file : files) {
				System.out.println("发现一个脚本化管理器 : " + file.getCanonicalPath());
				String copyFilePath = getCopyFilePath(file, srcDir, desDir);
				System.out.println("脚本生成位置 : " + copyFilePath);
				File copyFile = new File(copyFilePath);
				createFile(copyFile);
				copyFile(file, copyFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
