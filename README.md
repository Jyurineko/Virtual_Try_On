# mobile Virtual Dress-On Application
***
## Installation Steps
### Installation of Server
1. Remember Installation must in root enviroment.
2. Copy `upload_watchdog.py` on your desktop.
3. Flow the [TF-FLAME](https://github.com/TimoBolkart/TF_FLAME/tree/dependabot/pip/tensorflow-gpu-2.3.1](https://github.com/TimoBolkart/TF_FLAME "TF-FLAME") installation.
4. Flow the [face-alignment](https://github.com/1adrianb/face-alignment](https://github.com/1adrianb/face-alignment "face-alignment") installation.
5. Copy `gen_lmks_npy_4_Flame.py` into face-alignment/examples folder. I have already modified for FLAME. get rid of 0-17(face landmarks) landmarks which won't FLAME use. or you can modify `detect_landmarks_in_image.py` on your own porpose.
6. Because FLAME default use python3.6 to compile the project. when obj file is generated, `usemtl 'mtlname'` will not auto generated. if you directly install python3.6 in your system instead of using virtual enviroment. you can change codes in `/usr/local/lib/python3.6/site-packages/psbody/mesh/serialization/serialization.py`. 
For example (you can search the comment sentence "you can change xxxxxx" in this file to check where can you change: <pre># you can change your mtl name here 
fi.write('usemtl selfie_m\n')</pre>
7. In Desktop create the receive folder of server, of course you can change location by yourself, but remember to change location in `upload_watchdog.py` as well.:<pre>cd ~/Desktop
mkdir FileShare
cd FileShare
mkdir ForClient && mkdir Upload</pre>
8. Finally install Wine 6.0:<pre>apt install --install-recommends winehq-stable</pre>and download [HFS](https://www.rejetto.com/hfs/?f=dl "HFS") on desktop. <br>right click the HFS, and choose `Open With "Wine Windows Program Loader"` to open HFS.<br>drag "FileShare" folder into "Virtual File System" of HFS.<br> change Port to 2222, because in APK i choose this port-number to Port Forwarding. or you can change port-number in source code of this project.
9. If you are using WSL2 to run Ubuntu OS, i prefer to use [WSL2-auto-port-forward-python](https://github.com/itxq/wsl2-auto-port-forward-python "WSL2-auto-port-forward-python") tool(in Chinese).<br>Of course you can manuelly create "Inbound Rules" & "Outbound Rules" in "Windows Firewall with Advanced Security" for port forwarding from local Windows OS to WSL2.<br>and if you want use this application from outside, you must have public IP-Address, and set the Port Forward in your Router.
10. if you are just using Ubuntu OS directly. just set the Port Forward in your Router.
### Installation of Client
1. compile the source code here [Virtual-Try-On](https://github.com/Jyurineko/Virtual_Try_On "Virtual_Try_On") by yourself or just install APK.
## Change Log
### Version 2.2
1. User now can choose the favorite glasses and built models of head and glasses in one window.
2. pre-scale and trasnlate position of glasses model.
3. re-construction of UI, work flow.
4. User can re-choose the glasses and start new Fitting.
##### known issues:
1. server will auto run the image handle sequence, when server detect the image has been upload from client. But image isn't completely uploaded and stored in server. Next version of app is planning to deal with it.
***
### Version 2.1
1. "check" feature is added.
2. favorite "Glasses" list is realized. now User can see the different glasses types in onw list.
##### Upcomming features:
1. app must build head model and chooses glasses in one View Window.
2. glasses model must rescale & retranslate to fit head model.
***
### Version 2.0
1. complete the auto-run script, when server receive the image, it will automatically built the User's head model.
##### Upcomming features:
1. client must cheak first that obj has been generated, then client will download the files and prepare to construct in User's mobile device.
***
### Version 1.2
1. now can use system camera to take selfie.
2. rebuilt Http client service, now app won't show the download notifications instead of do it in background.
##### Upcomming features:
1. now it is only tested with existed obj file. server must handle the image automatically.
***
### Version 1.1
1. Http file server built in local machine or web server, now App can download nessary<br> files from server.
2. realize the app-in camera function.
##### Upcomming features:
1. user's picture upload.
2. automatic deal with the picture and output the result.
***
### Version 1.0
1. Create the basic UI interface, include Main page and 3D obj shower.
2. Obj \ Mtl \ Png files are built in apk's assets folder.
##### Upcomming features:
1. download & upload server functions.
***
