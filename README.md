![image](https://github.com/user-attachments/assets/33dab08a-a042-4bf3-9f16-c68fe3ff4556)For backend:
1. Setup IntelliJ 2021+ , Java Oracle SDK 17, maven 3.9.5, pgAdmin4 PostGreSQL
2. Modify the build option for user-local build: -Djps.track.ap.dependencies=false
 ![image](https://github.com/user-attachments/assets/be792954-4fd8-40c1-83f0-4b2ba1886628)
3. Settings -> Project Structure -> Modules -> Modify the correct SDK
![image](https://github.com/user-attachments/assets/b47ccb91-7ec2-4e30-8612-ef697d352956)
![image](https://github.com/user-attachments/assets/2d771ab5-15ff-4a6a-8223-ba7564a3dba2)
4. Setup a maven configuration, pointing to the main folder of the hoversprite-backend, setup command "clean install" for it.
![image](https://github.com/user-attachments/assets/4a60508a-3cf5-41c7-b05a-fdb6191bf703)
5. If there are errors, check the target module of the one that got errors, re-set it to be generated source root again.
![image](https://github.com/user-attachments/assets/231e25fe-f9cc-4564-b3d7-d256cd96fb57)

6. Remember to create a hoversprite db in PostgreSQL with the port 5432, modify your password & username so it connect to the database.
![image](https://github.com/user-attachments/assets/7925c17f-cf5f-4aea-957d-78b6315894f3)
7. Search for class "ProjectApplication" and run it. The app will be able to run by now.

EXTRA:
You want to create the account for sprayer and receptionist, simply import my Postman's json file to your Postman Console.
You can add or modify the role there for different name that you would like.
