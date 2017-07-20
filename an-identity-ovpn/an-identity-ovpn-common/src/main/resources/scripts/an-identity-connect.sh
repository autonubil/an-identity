#!/bin/sh
logger "AN-Connect"

if [ "$1" = "help" ]
then
        echo "an-identity-connect.sh v0.1 - OpenVPN sh connect script"
        echo "                           for use with an-identity server"
        echo ""
        echo "help - prints help"
        exit 1
fi


export USERNAME=$(echo $common_name |  grep -oP "^.*(?=@)")
export SOURCE=$(echo $common_name |  grep -oP "(?<=@).*")
export start_connect_time=$(stat -c %Y /etc/openvpn/ccd.extern/${common_name})


endpoint=http://10.18.0.1:8080/autonubil/api/ovpn/vpns/veb-onprem/client-config


PAYLOAD=$(echo "{\"sourceId\": \"${SOURCE}\",  \"username\": \"${USERNAME}\", \"password\": \"GetConfig\",  \"local\" : \"${ifconfig_local}\", \"localNetmask\" : \"${ifconfig_netmask}\", \"remote\" : \"${ifconfig_pool_remote_ip}\", \"remoteNetmask\" : \"${ifconfig_pool_netmask}\", \"connected\" : ${time_unix}, \"vpnPid\" : ${daemon_pid} }")

# logger "$endpoint ${PAYLOAD}"



STATUSCODE=$(curl --noproxy 127.0.0.1 -X POST $endpoint --data-binary "${PAYLOAD}" --header 'Content-Type: application/json' --silent --write-out '%{http_code}' -o /tmp/openvpn_cc_3551175ac58ae0db2ded3df43dcb0838.tmp  )

CURL_RESULT="$?"

if [ "$CURL_RESULT" -ne "0" ]; then
  logger "OpenVPN REST call to $endpoint failed with ${CURL_RESULT}"
  exit 1
fi


# logger "Status: ${STATUSCODE}"

if [ "${STATUSCODE}" -ne "200" ]; then
  logger "OpenVPN config for ${USERNAME} failed with ${STATUSCODE}"
  exit 1
fi

logger "OpenVPN Config received for ${USERNAME}"
exit 0
