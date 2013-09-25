#!/bin/bash

MYSQL="mysql --skip-column-names -u root -proot kikbak"

#defaults
code="bug"
street="Maciej St."


function usage {
echo "Usage: add_location -m merchant (-u user | -l latitude -g longitude ) [-q verification_code] [-s street]"
echo " like: add_location -m mudpuppy -u maciej" 
echo ""
}

function set_location {
 case $1 in
   "maciej")
     lat=51.1422638
     lgt=16.9662423
     ;;
   "ian")
     lat=
     lgt=
     ;; 
   "brian")
     lat=37.769868
     lgt=-122.4023801
     ;; 
   "chen")
     lat=
     lgt=
     ;; 
 esac
}

usage

while getopts "m:u:l:g:q:s:" OPTION
do
     case $OPTION in
         m)
             merchant=$OPTARG
             ;;
         u)
             who=$OPTARG
             ;;
         l)
             lat=$OPTARG
             ;;
         g)
             lgt=$OPTARG
             ;;
         q)
             code=$OPTARG
             ;;
         s)
             street=$OPTARG
             ;;
     esac
done

if [ -z "$merchant" ] ; then
  echo "Need to set merchant name"
  exit 1
fi

if [ -n "$who" ] ; then 
  set_location $who
fi

if [ -z "$lat" -o -z "$lgt" ] ; then
  echo "Need to set location or user"
  exit 1
fi


# find out id of merchant from name
id=`$MYSQL <<EOF
select id from merchant where name like "%${merchant}%"
EOF`

if [[ $id == *$'\n'* ]] ; then 
  echo "More than one merchant found matching '$merchant'"
  exit 1
fi

# print it
name=`$MYSQL <<EOF
select name from merchant where id = ${id}
EOF`

if [ -z "$name" ] ; then
   echo "Can not find merchant like $merchant"
   exit 1;
fi

echo "Adding location for $name - latitude:$lat longitude:$lgt"


# insert location
$MYSQL <<EOF
insert into location (address_1,   city,            state, zipcode, phone_number, verification_code,  merchant_id, latitude,  longitude) values 
                     ('${street}', 'San Francisco', 'CA',  '94114', 5555555555,   '${code}',          ${id},       ${lat},    ${lgt});
EOF

