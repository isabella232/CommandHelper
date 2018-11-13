/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.laytonsmith.PureUtilities.VirtualFS;

import com.laytonsmith.PureUtilities.Common.ArrayUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@FileSystemLayer.fslayer("file")
public class MemoryFileSystemLayer extends FileSystemLayer {

	public MemoryFileSystemLayer(VirtualFile path, VirtualFileSystem fileSystem, String symlink) {
		super(path, fileSystem, symlink);
		if(!MEM_FILE_SYSTEM.containsKey(fileSystem)) {
			MEM_FILE_SYSTEM.put(fileSystem, new HashMap<>());
		}
	}

	private static final Map<VirtualFileSystem, Map<String, byte[]>> MEM_FILE_SYSTEM = new HashMap<>();

	private Map<String, byte[]> getFS() {
		return MEM_FILE_SYSTEM.get(fileSystem);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		Map<String, byte[]> fs = getFS();
		if(fs.containsKey(path.getPath())) {
			return new ByteArrayInputStream(fs.get(path.getPath()));
		} else {
			throw new IOException("File does not exist");
		}
	}

	@Override
	public void writeByteArray(byte[] bytes) throws IOException {
		getFS().put(path.getPath(), bytes);
	}

	@Override
	public VirtualFile[] listFiles() throws IOException {
		Map<String, byte[]> fs = getFS();
		VirtualFile[] files = new VirtualFile[fs.size()];
		List<String> keySet = new ArrayList<>(fs.keySet());
		for(int i = 0; i < files.length; i++) {
			files[i] = new VirtualFile(keySet.get(i));
		}
		return files;
	}

	@Override
	public void delete() throws IOException {
		getFS().remove(path.getPath());
	}

	@Override
	public void deleteEventually() throws IOException {
		this.delete();
	}

	@Override
	public boolean exists() throws IOException {
		return getFS().containsKey(path.getPath());
	}

	@Override
	public boolean canRead() throws IOException {
		return true;
	}

	@Override
	public boolean canWrite() throws IOException {
		return true;
	}

	@Override
	public boolean isDirectory() throws IOException {
		return getFS().get(path.getPath()) == null;
	}

	@Override
	public boolean isFile() throws IOException {
		return !isDirectory();
	}

	@Override
	public void mkdirs() throws IOException {
		String[] parts = path.getPathParts();
		for(int i = 0; i < parts.length; i--) {
			String path = "";
			for(int j = 0; j < i; j++) {
				path += parts[j];
			}
			getFS().put(path, null);
		}
	}

	@Override
	public void createNewFile() throws IOException {
		getFS().put(path.getPath(), ArrayUtils.EMPTY_BYTE_ARRAY);
	}

}
