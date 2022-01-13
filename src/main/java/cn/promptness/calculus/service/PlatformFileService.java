package cn.promptness.calculus.service;

import cn.promptness.calculus.cache.LocalDbFileCache;
import cn.promptness.calculus.enums.FileRecordTypeEnum;
import cn.promptness.calculus.pojo.*;
import cn.promptness.calculus.utils.SnowflakeIdUtil;
import cn.promptness.calculus.utils.ZipUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.utils.DateUtils;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@Slf4j
public class PlatformFileService {

    @Resource
    private SnowflakeIdUtil snowflakeIdUtil;
    @Resource
    private LocalDbFileCache localDbFileCache;
    @Resource
    private AssetBillService assetBillService;

    @Value("${baseTempPath:../calculus/${spring.profiles.active}/file}")
    private String baseTempPath;
    @Value("${downloadHost:https://img1.fenqile.com}")
    private String downloadHost;

    private static final String REGEX_FLOAT = "^[-+]?([0-9]+)([.]([0-9]+))?$";
    private static final Pattern PATTERN = Pattern.compile(REGEX_FLOAT);

    @SneakyThrows
    public Map<String, List<ExpectSearchRsp>> searchCapitalExpectRepay(ExpectSearchReq expectSearch) {

        LocalDbFile localDbFile = localDbFileCache.getLocalDbFile(expectSearch.getLoanChannel(), expectSearch.getPaymentTime(), FileRecordTypeEnum.EXPECT);
        if (localDbFile != null) {
            return searchByDbFile(localDbFile, () -> getExpectKey(expectSearch), this::buildExpectResult);
        }

        FileRecord fileRecord = listFullRecord(expectSearch.getLoanChannel(), expectSearch.getPaymentTime(), 1);
        if (fileRecord == null) {
            return Maps.newHashMap();
        }

        // 当前文件的总目录
        String fileDirectory = String.format("%s/%s", baseTempPath, snowflakeIdUtil.nextId());
        try {
            Assert.isTrue(new File(fileDirectory).mkdirs(), "文件夹创建失败");

            LocalDbFile newLocalDbFile = buildLocalDbFile(fileDirectory, expectSearch.getLoanChannel(), expectSearch.getPaymentTime(), FileRecordTypeEnum.EXPECT);
            Map<String, List<ExpectSearchRsp>> resultMap = searchByRemoteFile(newLocalDbFile, fileRecord, fileDirectory,
                    this::getLineKey,
                    () -> getExpectKey(expectSearch),
                    this::buildExpectResult
            );
            localDbFileCache.cacheLocalDbFile(newLocalDbFile);
            return resultMap;
        } catch (RocksDBException | IOException exception) {
            FileUtils.forceDelete(new File(fileDirectory));
            throw exception;
        }
    }

    @SneakyThrows
    public Map<String, List<RealSearchRsp>> searchCapitalRealRepay(RealSearchReq realSearchReq) {

        LocalDbFile localDbFile = localDbFileCache.getLocalDbFile(realSearchReq.getLoanChannel(), realSearchReq.getRealRepayDate(), FileRecordTypeEnum.REAL);
        if (localDbFile != null) {
            return searchByDbFile(localDbFile, () -> getRealKey(realSearchReq), this::buildRealResult);
        }

        FileRecord fileRecord = listFullRecord(realSearchReq.getLoanChannel(), realSearchReq.getRealRepayDate(), 4);
        if (fileRecord == null) {
            return Maps.newHashMap();
        }

        // 当前文件的总目录
        String fileDirectory = String.format("%s/%s", baseTempPath, snowflakeIdUtil.nextId());
        try {
            Assert.isTrue(new File(fileDirectory).mkdirs(), "文件夹创建失败");

            LocalDbFile newLocalDbFile = buildLocalDbFile(fileDirectory, realSearchReq.getLoanChannel(), realSearchReq.getRealRepayDate(), FileRecordTypeEnum.REAL);

            Map<String, List<RealSearchRsp>> resultMap = searchByRemoteFile(newLocalDbFile, fileRecord, fileDirectory,
                    this::getLineKey,
                    () -> getRealKey(realSearchReq),
                    this::buildRealResult
            );
            localDbFileCache.cacheLocalDbFile(newLocalDbFile);
            return resultMap;
        } catch (RocksDBException | IOException exception) {
            FileUtils.forceDelete(new File(fileDirectory));
            throw exception;
        }
    }


    private LocalDbFile buildLocalDbFile(String fileDirectory, Integer loanChannel, Date businessDate, FileRecordTypeEnum fileRecordTypeEnum) {
        LocalDbFile newLocalDbFile = new LocalDbFile();
        newLocalDbFile.setLoanChannelId(loanChannel);
        newLocalDbFile.setBasePath(fileDirectory);
        newLocalDbFile.setBusinessDate(businessDate);
        newLocalDbFile.setFileRecordTypeEnum(fileRecordTypeEnum);
        newLocalDbFile.setInitTime(new Date());
        newLocalDbFile.setHitTime(new Date());
        return newLocalDbFile;
    }

    private Long stringToLong(String value) {
        if (StringUtils.isEmpty(value)) {
            return 0L;
        }
        Assert.isTrue(PATTERN.matcher(value).matches(), "数值类型转换异常" + value);
        return Long.valueOf(value);
    }

    private Integer stringToInt(String value) {
        if (StringUtils.isEmpty(value)) {
            return 0;
        }
        Assert.isTrue(PATTERN.matcher(value).matches(), "数值类型转换异常" + value);
        return Integer.valueOf(value);
    }

    private Date stringToDate(String value) {
        if (StringUtils.isEmpty(value)) {
            return Timestamp.valueOf("1970-01-01 00:00:00");
        }
        return DateUtils.parseDate(value, new String[]{"yyyy-MM-dd"});
    }

    private String getLineKey(String line) {
        String[] contentList = StringUtils.delimitedListToStringArray(line, "|");
        return contentList[6] + "-" + contentList[13];
    }

    private List<byte[]> getExpectKey(ExpectSearchReq expectSearch) {
        List<byte[]> keyList = Lists.newArrayList();
        for (ExpectSearchReq.ExpectSearchParam expectSearchParam : expectSearch.getExpectSearchParamList()) {
            for (int i = 1; i <= expectSearchParam.getLoanTerm(); i++) {
                keyList.add((expectSearchParam.getAssetId() + "-" + i).getBytes());
            }
        }
        return keyList;
    }

    private List<byte[]> getRealKey(RealSearchReq realSearchReq) {
        List<byte[]> keyList = Lists.newArrayList();
        for (RealSearchReq.RealSearchParam realSearchParam : realSearchReq.getRealSearchParamList()) {
            for (int i = realSearchParam.getMinRepayTerm(); i <= realSearchParam.getMaxRepayTerm(); i++) {
                keyList.add((realSearchParam.getAssetId() + "-" + i).getBytes());
            }
        }
        return keyList;
    }

    private Map<String, List<ExpectSearchRsp>> buildExpectResult(List<byte[]> resultList) {
        Map<String, List<ExpectSearchRsp>> expectSearchRspMap = Maps.newHashMap();

        for (byte[] result : resultList) {
            if (result == null) {
                continue;
            }
            String[] resultLine = StringUtils.delimitedListToStringArray(new String(result), "|");

            ExpectSearchRsp expectSearchRsp = new ExpectSearchRsp();
            expectSearchRsp.setLoanChannelId(stringToInt(resultLine[1]));
            expectSearchRsp.setExpectRepayDate(stringToDate(resultLine[3]));
            expectSearchRsp.setAssetId(resultLine[6]);
            expectSearchRsp.setPaymentTime(stringToDate(resultLine[9]));
            expectSearchRsp.setRepayTerm(stringToInt(resultLine[13]));
            expectSearchRsp.setExpectRepayAmount(stringToLong(resultLine[14]));
            expectSearchRsp.setExpectRepayPrincipal(stringToLong(resultLine[15]));
            expectSearchRsp.setExpectRepayFee(stringToLong(resultLine[16]));

            expectSearchRspMap.compute(resultLine[6], (key, value) -> {
                if (value == null) {
                    value = Lists.newArrayList();
                }
                value.add(expectSearchRsp);
                return value;
            });
        }
        return expectSearchRspMap;
    }


    private Map<String, List<RealSearchRsp>> buildRealResult(List<byte[]> resultList) {
        Map<String, List<RealSearchRsp>> realSearchRspMap = Maps.newHashMap();

        for (byte[] result : resultList) {
            if (result == null) {
                continue;
            }
            String[] resultLine = StringUtils.delimitedListToStringArray(new String(result), "|");

            RealSearchRsp realSearchRsp = new RealSearchRsp();
            realSearchRsp.setLoanChannelId(stringToInt(resultLine[1]));
            realSearchRsp.setRealRepayDate(stringToDate(resultLine[3]));
            realSearchRsp.setAssetId(resultLine[6]);

            realSearchRsp.setRepayTerm(stringToInt(resultLine[13]));
            realSearchRsp.setRealRepayAmount(stringToLong(resultLine[14]));
            realSearchRsp.setRealRepayPrincipal(stringToLong(resultLine[15]));
            realSearchRsp.setRealRepayFee(stringToLong(resultLine[16]));

            realSearchRsp.setRealRepayMulct(stringToLong(resultLine[20]));
            realSearchRsp.setRealRepayType(stringToInt(resultLine[21]));

            realSearchRspMap.compute(resultLine[6], (key, value) -> {
                if (value == null) {
                    value = Lists.newArrayList();
                }
                value.add(realSearchRsp);
                return value;
            });
        }
        return realSearchRspMap;
    }


    private <T> Map<String, List<T>> searchByDbFile(LocalDbFile localDbFile, KeyStructure keyStructure, ValueStructure<T> valueStructure) throws RocksDBException, IOException {
        // DB目录使用占用标识
        try (InputStream ignored = new FileInputStream(localDbFile.getLockPath())) {
            try (Options options = new Options().setCreateIfMissing(false)) {
                try (final RocksDB rocksDb = RocksDB.open(options, localDbFile.getDbPath())) {
                    List<byte[]> resultList = rocksDb.multiGetAsList(keyStructure.getKeyList());
                    return valueStructure.getValueMap(resultList);
                }
            }
        }
    }

    private <T> Map<String, List<T>> searchByRemoteFile(LocalDbFile localDbFile, FileRecord fileRecord, String fileDirectory, LineStructure lineStructure, KeyStructure keyStructure, ValueStructure<T> valueStructure) throws Exception {

        // 文件下载地址
        String platformFileUrl = fileRecord.getFplatformFileUrl();

        // 判断该文件是.zip还是.csv
        boolean isCsv = platformFileUrl.endsWith(".csv");
        File zipFile = assetBillService.downloadFile(downloadHost + platformFileUrl, isCsv ? fileDirectory + "/data.csv" : fileDirectory + "/data.zip");

        try (FileInputStream fileInputStream = new FileInputStream(zipFile)) {
            Assert.isTrue(Objects.equals(fileRecord.getFplatformFileMd5(), DigestUtils.md5Hex(fileInputStream)), "文件完整性校验失败" + new Gson().toJson(fileRecord));
        }
        // 解压后的文件
        File unZipDirectory = isCsv ? zipFile : getUnZipDirectory(fileDirectory, zipFile);
        File[] unZipFileList = isCsv ? new File[]{zipFile} : unZipDirectory.listFiles();
        Assert.isTrue(unZipFileList != null && unZipFileList.length > 0, "解压后文件为空{0}" + new Gson().toJson(fileRecord));

        try (final Options options = new Options().setCreateIfMissing(true)) {
            try (final RocksDB rocksDb = RocksDB.open(options, localDbFile.getDbPath())) {
                for (File file : unZipFileList) {
                    try (FileInputStream fileInputStream = new FileInputStream(file)) {
                        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream))) {
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                String key = lineStructure.getLineKey(line);
                                rocksDb.put(key.getBytes(), line.getBytes());
                            }
                        }
                    } finally {
                        FileUtils.deleteQuietly(file);
                    }
                }
                // searchKeys
                List<byte[]> keyList = keyStructure.getKeyList();
                List<byte[]> resultList = rocksDb.multiGetAsList(keyList);

                return valueStructure.getValueMap(resultList);
            }
        } finally {
            FileUtils.deleteQuietly(unZipDirectory);
        }
    }

    @SneakyThrows
    private File getUnZipDirectory(String filePath, File zipFile) {
        String destDir = filePath + "/csv";
        try {
            ZipUtil.unZip(zipFile.getPath(), destDir);
        } finally {
            FileUtils.forceDelete(zipFile);
        }
        return new File(destDir);
    }

    private FileRecord listFullRecord(Integer channelId, Date businessDate, int code) {
        try {
            return assetBillService.searchFileRecord(channelId, businessDate, code);
        } catch (Exception e) {
            log.error("{}-{}-{}-{}", channelId, businessDate, code, e.getMessage());
        }
        return null;
    }

    interface LineStructure {
        String getLineKey(String line);
    }

    interface KeyStructure {
        List<byte[]> getKeyList();
    }

    interface ValueStructure<T> {
        Map<String, List<T>> getValueMap(List<byte[]> resultList);
    }

}
