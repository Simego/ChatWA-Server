<%-- 
    Document   : index
    Created on : Apr 22, 2014, 7:37:04 PM
    Author     : Simego
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet" >
        <title>Whats Apop Chat</title>

        <style>
            button, .btn {
                outline: none !important;
            }

            .fade {
                -webkit-animation: fade 2s infinite linear;
                -moz-animation: fade 2s infinite linear;
                -o-animation: fade 2s infinite linear;
                animation: fade 2s infinite linear;
            }
            @-moz-keyframes fade {
                0% {
                    opacity: 0;
                }
                50% {
                    opacity: 100;
                }
                100% {
                    opacity: 0;
                }
            }
            @-webkit-keyframes fade {
                0% {
                    opacity: 0;
                }
                50% {
                    opacity: 100;
                }
                100% {
                    opacity: 0;
                }
            }
            @-o-keyframes fade {
                0% {
                    opacity: 0;
                }
                50% {
                    opacity: 100;
                }
                100% {
                    opacity: 0;
                }
            }
            @-ms-keyframes fade {
                0% {
                    opacity: 0;
                }
                50% {
                    opacity: 100;
                }
                100% {
                    opacity: 0;
                }
            }
            @keyframes fade {
                0% {
                    opacity: 0;
                }
                50% {
                    opacity: 100;
                }
                100% {
                    opacity: 0;
                }
            }
        </style>

    </head>
    <body>
        <div class="container" style="margin-top: 50px;">
            <div class="jumbotron" style="text-align: center;">
                <h1>
                    Whats Apop Messenger
                </h1>
                <h3>
                    Utilize o programa Desktop para acessar o chat e converse agora mesmo com seus amigos!
                </h3>
                <br/>
                <h4>
                    Atualmente temos
                    <span class="label label-success" style="padding: 20px 15px 10px 15px;"><span class="fade" style="font-size: 32px;">${accounts}</span></span> contas cadastradas.
                </h4>
                <br/>
                <button type="button" class="btn btn-primary btn-lg center-block">Baixar WhatsApop</button>
            </div>
        </div>
    </body>
</html>
