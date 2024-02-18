package com.securepass.apisecurepass.azure;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileInputStream;

@Service
public class Blob {

//    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=securepass;AccountKey=xRFzevfEIqCJ30rilIwKqI6SeZvugSOSYP8uij9RXUu6c9tqeCx3yxpWCZ94/PD0esQgieuBKSqb+ASt/k2tJQ==;EndpointSuffix=core.windows.net";
    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=codereconhecimento;AccountKey=taIOzH1zxOsWN8HfwOLZqnCl1EO9x53f0txAc6jhBDlLB2/tc2Rr23n9pscd+IuF14YLm+gZ/o+i+ASt0e1C7w==;EndpointSuffix=core.windows.net";


    // Criando função para enviar arquivo
    public static String UploadFileToBlob(MultipartFile arquivo, String nomeArquivo) {
        try {
            // Acessando os recursos da conta por meio da connection string
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

            // Acessando os dados de conexao com o blob
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            // Reconhecendo o container criado
            CloudBlobContainer container = blobClient.getContainerReference("containerblobstorage");
//            CloudBlobContainer container = blobClient.getContainerReference("securepasscontainer");

            // Criando uma referencia do novo arquivo que será gerado
            CloudBlockBlob blob = container.getBlockBlobReference(nomeArquivo);

            blob.upload(arquivo.getInputStream(), arquivo.getSize());

            return blob.getUri().toString();


        } catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
        }

        return null;
    }

    public static Boolean UpdateFileToBlob(MultipartFile novoArquivo, String idUsuario){
        try{
            // Acessando os recursos da conta por meio da connection string
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

            // Acessando os dados de conexao com o blob
            CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

            CloudBlobContainer container = blobClient.getContainerReference("containerblobstorage");

            // Procurando o arquivo original para alteracao
            for (ListBlobItem blobItem : container.listBlobs()){

                String[] path = blobItem.getUri().getPath().split("/");
                String nomeCompletoBlob = path[path.length - 1];
                String nomeBlob = nomeCompletoBlob.substring(0, nomeCompletoBlob.lastIndexOf('.'));

                // Verificando se o arquivo é igual
                if ( nomeBlob.equals( idUsuario )){

                    // Criando uma referencia do novo arquivo que será gerado
                    CloudBlockBlob blob = container.getBlockBlobReference( nomeCompletoBlob );
                    blob.upload( novoArquivo.getInputStream(), novoArquivo.getSize() );

                    return true;
                }

            }


        } catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
        }

        return false;
    }
}

