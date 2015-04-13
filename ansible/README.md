# Setup

Use vagrant up command to create the vm and look at the output to find ssh port

    $ vagrant up
    Bringing machine 'default' up with 'virtualbox' provider...
    ==> default: Importing base box 'ubuntu/trusty64'...
    ==> default: Matching MAC address for NAT networking...
    ...
    ==> default: Forwarding ports...
    default: 5601 => 5601 (adapter 1)
    default: 22 => 2222 (adapter 1)


Here ssh port is forwarded on port 2222
</br>
Add this in the file inventory : **ansible_ssh_port=2222**

    # inventory
    [monitoring]
    vagrant_test ansible_ssh_host=127.0.0.1 ansible_ssh_port=2222 ansible_connection=ssh ansible_ssh_user=vagrant

</br>
Use ansible to configure vm (ssh password and sudo password is **vagrant**):

    $ ansible-playbook -i inventory monitoring.yml
    SSH password:
    SUDO password[defaults to SSH password]:


    PLAY [monitoring] *************************************************************
    ...

If connection fails, you may have to connect to the vm first :

    $ ssh vagrant@127.0.0.1 -p 2222
    The authenticity of host '[127.0.0.1]:2222 ([127.0.0.1]:2222)' can't be established.
    ECDSA key fingerprint is 8c:eb:78:fd:42:4b:61:b4:4f:09:a9:a3:b1:38:0a:56.
    Are you sure you want to continue connecting (yes/no)? yes

Then you can use ansible.
</br>
When ansible ends, open your browser on http://localhost:5601
</br>
Choose monitoring for the index instead of logstash.
To start publishing event, use this command :

    vagrant ssh -c "while sleep 5; do
    curl -X POST --data 'numberOfMessages=10&timeToSpend=1' 'http://localhost:9292/messages' ; done"
