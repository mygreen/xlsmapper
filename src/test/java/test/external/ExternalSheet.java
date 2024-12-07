package test.external;

import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import com.gh.mygreen.xlsmapper.annotation.LabelledCellType;
import com.gh.mygreen.xlsmapper.annotation.XlsConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsDateTimeConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsEnumConverter;
import com.gh.mygreen.xlsmapper.annotation.XlsLabelledCell;
import com.gh.mygreen.xlsmapper.annotation.XlsSheet;

import test.external.PostalCode.PostalCellConverterFactory;

/**
 * サンプルのシート。
 * <p>外部パッケージの核に尿
 *
 * @author T.TSUCHIE
 *
 */
@XlsSheet(name = "外部パッケージ")
public class ExternalSheet {
    
    public static enum Role {
        Admin, Developer, Repoter
    }
    
    @NotEmpty
    @Length(max = 20)
    @XlsLabelledCell(label = "名前", type = LabelledCellType.Right)
    private String name;
    
    @XlsLabelledCell(label = "入社日付", type = LabelledCellType.Right)
    @XlsDateTimeConverter(excelPattern = "YYYY/MM/DD")
    private LocalDate joinedDate;
    
    @XlsLabelledCell(label = "ロール", type = LabelledCellType.Right)
    @XlsEnumConverter(ignoreCase = true)
    private Role role;
    
    @XlsLabelledCell(label = "郵便番号", type = LabelledCellType.Right)
    @XlsConverter(PostalCellConverterFactory.class)
    private PostalCode postalCode;

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LocalDate getJoinedDate() {
        return joinedDate;
    }
    
    public void setJoinedDate(LocalDate joinedDate) {
        this.joinedDate = joinedDate;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public PostalCode getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(PostalCode postalCode) {
        this.postalCode = postalCode;
    }
}
