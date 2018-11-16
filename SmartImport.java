//package src.oracle.odi.Exelon.SmartImport;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.io.File;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.core.persistence.transaction.support.ITransactionCallback;
import oracle.odi.core.persistence.transaction.support.TransactionTemplate;
import oracle.odi.publicapi.samples.SimpleOdiInstanceHandle;
import oracle.odi.impexp.EncodingOptions;
import oracle.odi.impexp.smartie.ISmartImportService;
import oracle.odi.impexp.smartie.OdiSmartImportException;
import oracle.odi.impexp.smartie.impl.SmartImportServiceImpl;
import oracle.odi.impexp.smartie.impl.SmartImportFileSupport;
import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.core.persistence.transaction.support.ITransactionCallback;
import oracle.odi.core.persistence.transaction.support.TransactionTemplate;
import oracle.odi.domain.project.OdiProject;
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.impexp.EncodingOptions;
import oracle.odi.impexp.smartie.ISmartExportService;
import oracle.odi.impexp.smartie.ISmartExportable;
import oracle.odi.impexp.smartie.impl.SmartExportServiceImpl;
import oracle.odi.publicapi.samples.SimpleOdiInstanceHandle;
import oracle.odi.core.OdiInstance;
import oracle.odi.core.config.MasterRepositoryDbInfo;
import oracle.odi.core.config.OdiInstanceConfig;
import oracle.odi.core.config.PoolingAttributes;
import oracle.odi.core.config.WorkRepositoryDbInfo;
import oracle.odi.core.persistence.transaction.ITransactionStatus;
import oracle.odi.core.persistence.transaction.support.DefaultTransactionDefinition;
import oracle.odi.core.security.Authentication;
import oracle.odi.domain.project.IOdiScenarioSourceContainer;
import oracle.odi.domain.project.OdiFolder;
import oracle.odi.domain.project.OdiProject;
import oracle.odi.domain.project.finder.IOdiFolderFinder;
import oracle.odi.domain.project.finder.IOdiProjectFinder;
import oracle.odi.domain.runtime.scenario.OdiScenario;
import oracle.odi.domain.runtime.scenario.finder.IOdiScenarioFinder;

public class SmartImport {
 
        private static String Project_Code;
        public static void main(String[] args) throws IOException, ParseException {

        String Url =           args[0];
        String Driver =        args[1];
        String Master_User =   args[2];
        String Master_Pass =   args[3];
        String WorkRep =       args[4];
        String Odi_User =      args[5];
        String Odi_Pass =      args[6];
        String ExportFolderPath = args[7];
        String ImportFile =    args[8];
        char[] ExportKey = "P@ssw0rd".toCharArray(); 

        final String fnameAndPath    = ExportFolderPath + File.separator + ImportFile;
        final EncodingOptions expeo = new EncodingOptions("1.0", "ISO8859_9",  "ISO-8859-9");


        Boolean ExportPackageScen      = true;
        Boolean ExportInterfaceScen    = true;
        Boolean ExportProcedureScen    = true;
        Boolean ExportVariableScen     = false;
        Boolean RecursiveExport        = true;
        Boolean OverWriteFile          = true;
        Boolean ExportWithoutCipherData  = false;
    
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        Date date =df.parse("10-09-2018");

        MasterRepositoryDbInfo masterRepoInfo = new MasterRepositoryDbInfo(Url, Driver, Master_User,Master_Pass.toCharArray(), new PoolingAttributes());

        WorkRepositoryDbInfo workRepoInfo = new WorkRepositoryDbInfo(WorkRep, new PoolingAttributes());
        OdiInstance odiInstance=OdiInstance.createInstance(new OdiInstanceConfig(masterRepoInfo,workRepoInfo));
        Authentication auth = odiInstance.getSecurityManager().createAuthentication(Odi_User,Odi_Pass.toCharArray());
        odiInstance.getSecurityManager().setCurrentThreadAuthentication(auth);
        ITransactionStatus trans = odiInstance.getTransactionManager().getTransaction(new DefaultTransactionDefinition());
        System.out.println( " Successfully COnnected to ODI Work Repository");

                    
        TransactionTemplate transaction = new TransactionTemplate (odiInstance.getTransactionManager ());
                    
            transaction.execute (new ITransactionCallback ()
                {
                    public Object doInTransaction (ITransactionStatus pStatus)
                    {
                        boolean flag = true;
                                            
                        ISmartImportService smartImpServ = new SmartImportServiceImpl (odiInstance);
                                
                        try {
                            
                            SmartImportFileSupport sifs = SmartImportFileSupport.getInstance();
                                    
                            flag = sifs.isSmartExportFile (fnameAndPath);
                            System.out.println("\n[I N F O] isSmartExportFile returns " + flag + "\n");
                                    
                            // If .xml file exists, then run the Smart Import from XML file
                            smartImpServ.importObjectsFromXml (fnameAndPath, ExportKey, ExportWithoutCipherData);
                            smartImpServ.setMatchedFCODefaultImportAction("Dev_ODI_Project", 1);
                          // smartImpServ.setMatchedFCODefaultImportAction(java.lang.String pFCOObjType, int pSmartImportAction)
                            
                                    
                        System.out.println( " Successfully Imported to ODI Work Repository");
                        } catch (IOException e) {
                            e.printStackTrace();
                                    
                        } catch (OdiSmartImportException e) {
                            e.printStackTrace();            
                        }

                        return null;
                    } // doInTransaction 
                }
            ); // transaction.execute 
        } // main
}
