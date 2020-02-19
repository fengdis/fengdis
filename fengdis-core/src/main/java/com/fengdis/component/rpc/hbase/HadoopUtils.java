package com.fengdis.component.rpc.hbase;

import com.fengdis.util.DateUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

@Component
@ConditionalOnBean(HbaseConfig.class)
public class HadoopUtils {

	private static String[] suf = {"csv","txt","doc","docx","xls","xlsx","ppt","pptx"};
	private static final String ROOT = "/";

	@Autowired
	private Connection connection;

	@Autowired
	private FileSystem fs;


	/*****************hdfs***************8*/
	/**
	 * 上传文件
	 * @param filePath
	 * @param dir
	 * @throws Exception
	 */
	public void upload(String filePath, String dir) throws Exception {
		InputStream in = new BufferedInputStream(new FileInputStream(filePath));
		OutputStream out = fs.create(new Path(ROOT + dir), new Progressable() {

			@Override
			public void progress() {
				//System.out.println("ok");
			}
		});
		IOUtils.copyBytes(in, out, 4096, true);
	}
	/**
	 * 已流形式上传
	 * @param in
	 * @param dir
	 * @throws Exception
	 */
	public void upload(InputStream in, String dir) throws Exception {
		OutputStream out = fs.create(new Path(dir), new Progressable() {
			@Override
			public void progress() {
				//System.out.println("ok");
			}
		});
		IOUtils.copyBytes(in, out, 4096, true);
	}
	/**
	 * 下载文件
	 * @param path
	 * @param local
	 * @throws Exception
	 */
	public void downLoad(String path,String local) throws Exception {
		FSDataInputStream in = fs.open(new Path(path));
		OutputStream out = new FileOutputStream(local);
		IOUtils.copyBytes(in, out, 4096, true);
	}
	/**
	 * 重命名文件
	 * @param src
	 * @param dst
	 * @throws Exception
	 */
	public void rename(String src,String dst) throws Exception {
		fs.rename(new Path(src), new Path(dst));
	}

	/**
	 * 创建文件夹
	 * @param dir
	 * @throws Exception
	 */
	public void mkdir(String dir) throws Exception {
		if (!fs.exists(new Path(dir))) {
			fs.mkdirs(new Path(dir));
		}
	}
	/**
	 * 删除文件及文件夹
	 * @param name
	 * @throws Exception
	 */
	public void delete(String name) throws Exception {
		fs.delete(new Path(name), true);
	}

	/**
	 * 查询文件夹
	 * @param dir
	 * @return
	 * @throws Exception
	 */
	/*public List<FileSystemVo> queryAll(String dir) throws Exception {
		FileStatus[] files = fs.listStatus(new Path(dir));
		List<FileSystemVo> fileVos = new ArrayList<FileSystemVo>();
		FileSystemVo f = null;
		for (int i = 0; i < files.length; i++) {
			f = new FileSystemVo();
			if (files[i].isDir()) {
				f.setName(files[i].getPath().getName());
				f.setType("D");
				f.setDate(DateUtil.longToString("yyyy-MM-dd HH:mm", files[i].getModificationTime()));
				f.setNamep(files[i].getPath().getName());
			} else {
				f.setName(files[i].getPath().getName());
				f.setType("F");
				f.setDate(DateUtil.longToString("yyyy-MM-dd HH:mm", files[i].getModificationTime()));
				f.setSize(BaseUtils.FormetFileSize(files[i].getLen()));
				f.setNamep(f.getName().substring(0, f.getName().lastIndexOf(".")));
				String s=FileUtils.getFileSufix(f.getName());
				for (int j = 0; j < suf.length; j++) {
					if (s.equals(suf[j])) {
						f.setViewflag("Y");
						break;
					}
				}
			}
			fileVos.add(f);
		}
		return fileVos;
	}*/
	/**
	 * 移动或复制文件
	 * @param path
	 * @param dst
	 * @param src true 移动文件;false 复制文件
	 * @throws Exception
	 */
	/*public void copy(String[] path, String dst,boolean src) throws Exception {
		Path[] paths = new Path[path.length];
		for (int i = 0; i < path.length; i++) {
			paths[i]=new Path(path[i]);
		}
		FileUtil.copy(fs, paths, fs, new Path(dst), src, true, conf);
	}*/
	
	public static void main(String[] args) throws Exception {
		HadoopUtils hdfsDB = new HadoopUtils();
		hdfsDB.mkdir(ROOT+"weir33/qq");

		// String path = "C://Users//Administrator//Desktop//jeeshop-jeeshop-master.zip";
		// hdfsDB.upload(path, "weir/"+"jeeshop.zip");
		// hdfsDB.queryAll(ROOT);
//		hdfsDB.visitPath("hdfs://h1:9000/weir");
//		for (Menu menu : menus) {
//			System.out.println(menu.getName());
//			System.out.println(menu.getPname());
//		}
//		hdfsDB.delete("weirqq");
//		hdfsDB.mkdir("/weirqq");
//		hdfsDB.tree("/admin");
		System.out.println("ok");
	}


	/*****************hbase***************8*/

	public TableName[] listTable() throws Exception {
//		HBaseAdmin admin = new HBaseAdmin(connection);
		Admin admin = connection.getAdmin();
		TableName[] tableNames = admin.listTableNames();
		admin.close();
		return tableNames;
	}

	public void deleteAllTable() throws Exception{
//		HBaseAdmin admin = new HBaseAdmin(connection);
		Admin admin = connection.getAdmin();
		TableName[] tableNames = admin.listTableNames();
		for (int i = 0; i < tableNames.length; i++) {
//			admin.disableTable(tableNames[i].getNameAsString());
//			admin.deleteTable(tableNames[i].getNameAsString());
			admin.disableTable(tableNames[i]);
			admin.deleteTable(tableNames[i]);
		}
		admin.close();
	}

	public void createTable(String tableName,String[] fams,int version) throws Exception {
//		HBaseAdmin admin = new HBaseAdmin(connection);
		Admin admin = connection.getAdmin();
		TableName tn = TableName.valueOf(tableName);
		if (admin.tableExists(tn)) {
			admin.disableTable(tn);
			admin.deleteTable(tn);
		}
		/*if (admin.tableExists(tableName)) {
			admin.disableTable(tableName);
			admin.deleteTable(tableName);
		}*/
		HTableDescriptor tableDescriptor = null;
		HColumnDescriptor hd = null;
		for (int i = 0; i < fams.length; i++) {
			tableDescriptor = new HTableDescriptor(tn);
			hd = new HColumnDescriptor(fams[i]);
			hd.setMaxVersions(version);
			tableDescriptor.addFamily(hd);
			admin.createTable(tableDescriptor);
		}
		admin.close();
	}

	public void delTable(String tableName) throws Exception {
//		HBaseAdmin admin = new HBaseAdmin(connection);
		Admin admin = connection.getAdmin();
		TableName tn = TableName.valueOf(tableName);
		/*if (admin.tableExists(tableName)) {
			admin.disableTable(tableName);
			admin.deleteTable(tableName);
		}*/
		if (admin.tableExists(tn)) {
			admin.disableTable(tn);
			admin.deleteTable(tn);
		}
		admin.close();
	}

	public long getGid(String row) throws Exception {
		Table table_gid = connection.getTable(TableName.valueOf("gid"));
//		HTable table_gid = new HTable(TableName.valueOf("gid"), connection);
		long id = table_gid.incrementColumnValue(Bytes.toBytes(row), Bytes.toBytes("gid"), Bytes.toBytes(row), 1);
		table_gid.close();
		return id;
	}

	public void add(String tableName, String rowKey, String family, String qualifier, String value) throws IOException {
		//连接到table
//		HTable table = new HTable(TableName.valueOf(tableName), connection);
		Table table = connection.getTable(TableName.valueOf(tableName));
		Put put = new Put(Bytes.toBytes(rowKey));
//		put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
		put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
		table.put(put);
		table.close();
	}

	public void add(String tableName, Long rowKey, String family, Long qualifier, String value) throws IOException {
		//连接到table
//		HTable table = new HTable(TableName.valueOf(tableName), connection);
		Table table = connection.getTable(TableName.valueOf(tableName));
		Put put = new Put(Bytes.toBytes(rowKey));
//		put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
		put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
		table.put(put);
		table.close();
	}

	public void add(String tableName, Long rowKey01,Long rowKey02, String family, String qualifier, Long value) throws IOException {
		//连接到table
//		HTable table = new HTable(TableName.valueOf(tableName), connection);
		Table table = connection.getTable(TableName.valueOf(tableName));
		Put put = new Put(Bytes.add(Bytes.toBytes(rowKey01), Bytes.toBytes(rowKey02)));
		if (qualifier!=null) {
//			put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
		}else{
//			put.add(Bytes.toBytes(family), null, Bytes.toBytes(value));
			put.addColumn(Bytes.toBytes(family), null, Bytes.toBytes(value));
		}
		table.put(put);
		table.close();
	}

	public void add(String tableName, Long rowKey01,Long rowKey02,Long rowKey03, String family, String qualifier, Long value01, Long value02) throws IOException {
		//连接到table
//		HTable table = new HTable(TableName.valueOf(tableName), connection);
		Table table = connection.getTable(TableName.valueOf(tableName));
		Put put = new Put(Bytes.add(Bytes.toBytes(rowKey01), Bytes.toBytes(rowKey02), Bytes.toBytes(rowKey03)));
		if (qualifier!=null) {
//			put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.add(Bytes.toBytes(value01), Bytes.toBytes(value02)));
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.add(Bytes.toBytes(value01), Bytes.toBytes(value02)));
		}else{
//			put.add(Bytes.toBytes(family), null, Bytes.add(Bytes.toBytes(value01), Bytes.toBytes(value02)));
			put.addColumn(Bytes.toBytes(family), null, Bytes.add(Bytes.toBytes(value01), Bytes.toBytes(value02)));
		}
		table.put(put);
		table.close();
	}

	public void add(String tableName, Long rowKey01,Long rowKey02, String family, String qualifier, String value) throws IOException {
		//连接到table
//		HTable table = new HTable(TableName.valueOf(tableName), connection);
		Table table = connection.getTable(TableName.valueOf(tableName));
		Put put = new Put(Bytes.add(Bytes.toBytes(rowKey01), Bytes.toBytes(rowKey02)));
		if (qualifier!=null) {
//			put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
			put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
		}else{
//			put.add(Bytes.toBytes(family), null, Bytes.toBytes(value));
			put.addColumn(Bytes.toBytes(family), null, Bytes.toBytes(value));
		}
		table.put(put);
		table.close();
	}
	/**
	 * 添加数据
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param qualifier
	 * @param value
	 * @throws IOException
	 */
	public void add(String tableName, Long rowKey, String family, String qualifier, String value) throws IOException {
		//连接到table
//		HTable table = new HTable(TableName.valueOf(tableName), connection);
		Table table = connection.getTable(TableName.valueOf(tableName));
		Put put = new Put(Bytes.toBytes(rowKey));
//		put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
		put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
		table.put(put);
		table.close();
	}
	/**
	 * 添加数据
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param qualifier
	 * @param value
	 * @throws IOException
	 */
	public void add(String tableName, Long rowKey, String family, String qualifier, Long value) throws IOException {
		//连接到table
//		HTable table = new HTable(TableName.valueOf(tableName), connection);
		Table table = connection.getTable(TableName.valueOf(tableName));
		Put put = new Put(Bytes.toBytes(rowKey));
//		put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
		put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
		table.put(put);
		table.close();
	}
	/**
	 * 添加数据
	 * @param tableName
	 * @param rowKey
	 * @param family
	 * @param qualifier
	 * @param value
	 * @throws IOException
	 */
	public void add(String tableName, String rowKey, String family, String qualifier, Long value) throws IOException {
		//连接到table
//		HTable table = new HTable(TableName.valueOf(tableName), connection);
		Table table = connection.getTable(TableName.valueOf(tableName));
		Put put = new Put(Bytes.toBytes(rowKey));
//		put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
		put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
		table.put(put);
		table.close();
	}
	/**
	 * 根据row删除数据
	 * @param tableName
	 * @param rowKey
	 * @throws Exception
	 */
	public void deleteRow(String tableName, String[] rowKey) throws Exception {
//		HTable table = new HTable(TableName.valueOf(tableName), connection);
		Table table = connection.getTable(TableName.valueOf(tableName));
		List<Delete> list = new ArrayList<Delete>();
		for (int i = 0; i < rowKey.length; i++) {
			Delete delete = new Delete(Bytes.toBytes(Long.valueOf(rowKey[i])));
			list.add(delete);
		}
		table.delete(list);
		table.close();
	}

	public void deleteColumns(String tableName,Long rowKey,String family, Long qualifier) throws Exception {
//		HTable table = new HTable(TableName.valueOf(tableName), connection);
		Table table = connection.getTable(TableName.valueOf(tableName));
		Delete delete = new Delete(Bytes.toBytes(rowKey));
//		delete.deleteColumns(Bytes.toBytes(family), Bytes.toBytes(qualifier));
		delete.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
		table.delete(delete);
		table.close();
	}
	public void deleteRow(String tableName,Long rowKey01,Long rowKey02) throws Exception {
//		HTable table = new HTable(TableName.valueOf(tableName), connection);
		Table table = connection.getTable(TableName.valueOf(tableName));
		Delete delete = new Delete(Bytes.add(Bytes.toBytes(rowKey01), Bytes.toBytes(rowKey02)));
		table.delete(delete);
		table.close();
	}

	public long getIdByUsername(String name) {
		long id = 0;
		try {
//			HTable table = new HTable(TableName.valueOf("user_id"), connection);
			Table table = connection.getTable(TableName.valueOf("user_id"));
			Get get = new Get(Bytes.toBytes(name));
			get.addColumn(Bytes.toBytes("id"), Bytes.toBytes("id"));
			Result rs = table.get(get);
			byte[] value = rs.getValue(Bytes.toBytes("id"), Bytes.toBytes("id"));
			id = Bytes.toLong(value);
			table.close();
		} catch (IOException e) {
			e.printStackTrace();
			return id;
		}
		return id;
	}

	public boolean checkUsername(String name) {
		try {
//			HTable table = new HTable(TableName.valueOf("user_id"), connection);
			Table table = connection.getTable(TableName.valueOf("user_id"));
			Get get = new Get(Bytes.toBytes(name));
			table.exists(get);
			if (table.exists(get)) {
				table.close();
				return true;
			}else{
				table.close();
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getUserNameById(long id) {
		String name = null;
		try {
//			HTable table = new HTable(TableName.valueOf("id_user"), connection);
			Table table = connection.getTable(TableName.valueOf("id_user"));
			Get get = new Get(Bytes.toBytes(id));
			get.addColumn(Bytes.toBytes("user"), Bytes.toBytes("name"));
			Result rs = table.get(get);
			byte[] value = rs.getValue(Bytes.toBytes("user"), Bytes.toBytes("name"));
			name = Bytes.toString(value);
			table.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return name;
	}

	public String getStringById(String tableName,Long rowKey,String family,String qualifier) {
		String name = null;
		try {
//			HTable table = new HTable(TableName.valueOf(tableName), connection);
			Table table = connection.getTable(TableName.valueOf(tableName));
			Get get = new Get(Bytes.toBytes(rowKey));
			get.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
			Result rs = table.get(get);
			byte[] value = rs.getValue(Bytes.toBytes(family), Bytes.toBytes(qualifier));
			name = Bytes.toString(value);
			table.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return name;
	}
	/**
	 * 通过目录名获取ID
	 * @param name
	 * @return
	 */
	public long getIdByDirName(String name) {
		long id = 0;
		try {
//			HTable table = new HTable(TableName.valueOf("hdfs_name"), connection);
			Table table = connection.getTable(TableName.valueOf("hdfs_name"));
			Get get = new Get(name.getBytes());
			get.addColumn(Bytes.toBytes("id"), Bytes.toBytes("id"));
			Result rs = table.get(get);
			byte[] value = rs.getValue(Bytes.toBytes("id"), Bytes.toBytes("id"));
			id = Bytes.toLong(value);
			table.close();
		} catch (IOException e) {
			e.printStackTrace();
			return id;
		}
		return id;
	}

	public boolean checkEmail(String email) throws Exception {
//		HTable table = new HTable(TableName.valueOf("email_user"), connection);
		Table table = connection.getTable(TableName.valueOf("email_user"));
		Get get = new Get(Bytes.toBytes(email));
		get.addColumn(Bytes.toBytes("user"), Bytes.toBytes("userid"));
		Result rs = table.get(get);
		byte[] value = rs.getValue(Bytes.toBytes("user"), Bytes.toBytes("userid"));
		table.close();
		if(value!=null){
			return true;
		}else {
			return false;
		}
	}

	public long checkUser(String userName,String pwd) throws Exception {
		long id = getIdByUsername(userName);
		if (id==0) {
			return 0;
		}
//		HTable table = new HTable(TableName.valueOf("id_user"), connection);
		Table table = connection.getTable(TableName.valueOf("id_user"));
		Get get = new Get(Bytes.toBytes(id));
		get.addColumn(Bytes.toBytes("user"), Bytes.toBytes("pwd"));
		Result rs = table.get(get);
		byte[] value = rs.getValue(Bytes.toBytes("user"), Bytes.toBytes("pwd"));
		if (pwd.equals(Bytes.toString(value))) {
			table.close();
			return id;
		}
		table.close();
		return 0;
	}

	public void queryAll(String tableName) throws Exception {
//		HTable table = new HTable(TableName.valueOf(tableName), connection);
		Table table = connection.getTable(TableName.valueOf(tableName));
		ResultScanner rs = table.getScanner(new Scan());
		for (Result result : rs) {
			System.out.println("rowkey" +result.getRow());
			for (Cell cell : result.rawCells()) {
				System.out.println("family"+new String(cell.getFamilyArray()));
				System.out.println("Qualifier"+new String(cell.getQualifierArray()));
				System.out.println("value"+new String(cell.getValueArray()));
			}
		}
		table.close();
	}

	public void queryAllHDFS(String username) throws Exception {
//		HTable table = new HTable(TableName.valueOf("hdfs"), connection);
		Table table = connection.getTable(TableName.valueOf("hdfs"));
		ResultScanner rs = table.getScanner(new Scan());
		for (Result result : rs) {
			System.out.println("rowkey" +result.getRow());
			for (Cell cell : result.rawCells()) {
				System.out.println("family"+new String(cell.getFamilyArray()));
				System.out.println("Qualifier"+new String(cell.getQualifierArray()));
				System.out.println("value"+new String(cell.getValueArray()));
			}
		}
		table.close();
	}

	public void delByDir(String dir) throws Exception {
//		HTable fileTable = new HTable(TableName.valueOf("filesystem"), connection);
		Table fileTable = connection.getTable(TableName.valueOf("filesystem"));
		Scan scan = new Scan();
		Filter filter = new QualifierFilter(CompareFilter.CompareOp.LESS_OR_EQUAL, new BinaryComparator(Bytes.toBytes(dir)));
		scan.setFilter(filter);
		ResultScanner rs = fileTable.getScanner(scan);
		for (Result r : rs) {
			fileTable.delete(new Delete(r.getRow()));
		}
		fileTable.close();
	}

	public boolean follow(String oname,String dname) throws Exception {
		long oid = this.getIdByUsername(oname);
		long did = this.getIdByUsername(dname);
		if (oid == 0 || did == 0 || oid == did){
			return false;
		}
		this.add("follow", oid, "name", did, dname);

		this.add("followed", did, oid, "userid", null, oid);
		return true;
	}
	public boolean unfollow(String oname,String dname) throws Exception {
		long oid = this.getIdByUsername(oname);
		long did = this.getIdByUsername(dname);
		if (oid == 0 || did == 0 || oid == did){
			return false;
		}
		this.deleteColumns("follow", oid, "name", did);

		this.deleteRow("followed", did, oid);
		return true;
	}
	/**
	 * 获取关注的用户
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public Set<String> getFollow(String username) throws Exception {
		Set<String> set = new HashSet<String>();
		long id = this.getIdByUsername(username);
//		HTable table = new HTable(TableName.valueOf("follow"), connection);
		Table table = connection.getTable(TableName.valueOf("follow"));
		Get get = new Get(Bytes.toBytes(id));
		Result rs = table.get(get);
		for (Cell cell : rs.rawCells()) {
			set.add(Bytes.toString(CellUtil.cloneValue(cell)));
		}
		return set;
	}
	/**
	 * 分享文件及文件夹
	 * @param username
	 * @param path
	 * @param shareusername
	 * @throws Exception
	 */
	public void share(String dir,String username,String[] path,String[] type,String shareusername) throws Exception {
		long uid = getIdByUsername(username);
		for (int i = 0; i < path.length; i++) {
			long id = getGid("shareid");
			add("share", uid,id, "content", "dir", dir);
			add("share", uid,id, "content", "type", type[i]);
			add("share", uid,id, "content", "path", path[i]);
			add("share", uid,id, "content", "ts", DateUtils.dateTime2String(new Date(),DateUtils.FMT_yyyyMMddHHmmss));

			long suid = getIdByUsername(shareusername);
			add("shareed", suid,uid,id, "shareid", null, uid,id);
		}
	}

	/**
	 * 新增记事本
	 * @param username
	 * @param content
	 * @throws Exception
	 */
	public void addbook(String username,String content) throws Exception {
		long uid = getIdByUsername(username);
		long id = getGid("bookid");
		add("book", uid, id, "content", null, content);
	}

}
