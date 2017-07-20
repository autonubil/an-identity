#!/bin/sh
logger "AN-Authenticate ${time_unix}"

if [ "$1" = "" ] || [ "$1" = "help" ]
then
        echo "an-identity-auth.sh v0.1 - OpenVPN sh authentication script"
        echo "                           for use with an-identity server"
        echo ""
        echo "help - prints help"
        exit 1
fi

endpoint=http://10.18.0.1:8080/autonubil/api/ovpn/vpns/veb-onprem/authenticate
readarray -t lines < $1

export USERNAME=${lines[0]}
export PASSWORD=${lines[1]}
export USERNAME_CN=$(echo $common_name |  grep -oP "^.*(?=@)")
export SOURCE=$(echo $common_name |  grep -oP "(?<=@).*")

if [ "$USERNAME_CN" != "$USERNAME" ]; then
 logger "OpenVPN authentication for ${USERNAME} does not match CN ${USERNAME_CN}"
 exit 1
fi

if [ -z "$time_unix" ]; then
   echo "push-reset" > "/etc/openvpn/ccd.extern/${common_name}"
fi;

export start_connect_time=$(stat -c %Y /etc/openvpn/ccd.extern/${common_name})


PAYLOAD=$(echo "{\"sourceId\": \"${SOURCE}\",  \"username\": \"${USERNAME}\", \"password\": \"${PASSWORD}\",  \"local\" : \"${ifconfig_local}\", \"localNetmask\" : \"${ifconfig_netmask}\", \"remote\" : \"${ifconfig_pool_remote_ip}\", \"remoteNetmask\" : \"${ifconfig_pool_netmask}\", \"connected\": ${start_connect_time}, \"vpnPid\": ${daemon_pid}  }" )

# logger "$endpoint ${PAYLOAD}"


STATUSCODE=$(curl --noproxy 127.0.0.1 -X POST $endpoint --data-binary "${PAYLOAD}" --header 'Content-Type: application/json' --silent --write-out '%{http_code}')
CURL_RESULT="$?"

#  -o /etc/openvpn/ccd.extern/${common_name} )

if [ "$CURL_RESULT" -ne "0" ]; then
  logger "OpenVPN REST call to $endpoint failed with ${CURL_RESULT}"
  exit 1
fi


# logger "Status: ${STATUSCODE}"

if [ "${STATUSCODE}" -ne "200" ]; then
  logger "OpenVPN authentication for ${USERNAME} failed with ${STATUSCODE}"
  exit 1
fi

if [ -z "$time_unix" ]; then
 logger "OpenVPN authenticated ${USERNAME}"
else
 logger "OpenVPN re-authenticated ${USERNAME}"
fi;
exit 0
