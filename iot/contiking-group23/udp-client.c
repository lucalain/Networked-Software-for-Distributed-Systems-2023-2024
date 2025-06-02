#include "contiki.h"
#include "net/routing/routing.h"
#include "random.h"
#include "net/netstack.h"
#include "net/ipv6/simple-udp.h"

#include "sys/log.h"
#define LOG_MODULE "App"
#define LOG_LEVEL LOG_LEVEL_INFO

#define WITH_SERVER_REPLY 1
#define UDP_CLIENT_PORT 8765
#define UDP_SERVER_PORT 5678

#define MAX_READINGS 10
#define SEND_INTERVAL (60 * CLOCK_SECOND)
#define FAKE_TEMPS 5

static float readings[MAX_READINGS];
static unsigned next_reading = 0;
static bool wasDisconnected = false;

static struct simple_udp_connection udp_conn;

/*---------------------------------------------------------------------------*/
PROCESS(udp_client_process, "UDP client");
AUTOSTART_PROCESSES(&udp_client_process);
/*---------------------------------------------------------------------------*/
static unsigned
get_temperature()
{
    static unsigned fake_temps[FAKE_TEMPS] = {30, 25, 20, 15, 10};
    return fake_temps[random_rand() % FAKE_TEMPS];
}
/*---------------------------------------------------------------------------*/
static void
udp_rx_callback(struct simple_udp_connection *c,
                const uip_ipaddr_t *sender_addr,
                uint16_t sender_port,
                const uip_ipaddr_t *receiver_addr,
                uint16_t receiver_port,
                const uint8_t *data,
                uint16_t datalen)
{
    // ...
}
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(udp_client_process, ev, data)
{
    static struct etimer periodic_timer;
    static struct etimer timer_after_avg_send;
    static float temp;
    static uip_ipaddr_t dest_ipaddr;

    PROCESS_BEGIN();

    /* Initialize temperature buffer */
    for (int i = 0; i < MAX_READINGS; i++)
    {
        readings[i] = 0;
    }

    simple_udp_register(&udp_conn, UDP_CLIENT_PORT, NULL,
                        UDP_SERVER_PORT, udp_rx_callback);

    etimer_set(&periodic_timer, SEND_INTERVAL);

    while (1)
    {

        PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&periodic_timer));

        temp = (float) get_temperature();

        LOG_INFO("Generating temp %f", temp);
        LOG_INFO_("\n");

        if (NETSTACK_ROUTING.node_is_reachable() && NETSTACK_ROUTING.get_root_ipaddr(&dest_ipaddr))
        {

            if (wasDisconnected)
            {
                /* Compute average */
                float average;
                float sum = 0;
                unsigned no = 0;
                for (int i = 0; i < MAX_READINGS; i++)
                {
                    if (readings[i] != 0)
                    {
                        sum = sum + readings[i];
                        no++;
                    }
                }
                average = sum / ((float)no);

                for (int i = 0; i < MAX_READINGS; i++)
                {
                    readings[i] = 0;
                }
                next_reading = 0;
                wasDisconnected = false;
                
                LOG_INFO("Sending average %f to ", average);
                LOG_INFO_6ADDR(&dest_ipaddr);
                LOG_INFO_("\n");
                simple_udp_sendto(&udp_conn, &average, sizeof(average), &dest_ipaddr);
                
                etimer_set(&timer_after_avg_send, SEND_INTERVAL);

                PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&timer_after_avg_send));
            }
        
            /* Send to DAG root */
            LOG_INFO("Sending single temperature %f to ", temp);
            LOG_INFO_6ADDR(&dest_ipaddr);
            LOG_INFO_("\n");

            simple_udp_sendto(&udp_conn, &temp, sizeof(temp), &dest_ipaddr);
        }
        else
        {
            /* Add reading */
            readings[next_reading++] = temp;
            if (next_reading == MAX_READINGS)
            {
                next_reading = 0;
            }
            LOG_INFO("Batching local readings");
            LOG_INFO_("\n");
            wasDisconnected = true;
        }

        /* Jitter */
        etimer_set(&periodic_timer, SEND_INTERVAL - CLOCK_SECOND + (random_rand() % (2 * CLOCK_SECOND)));
    }

    PROCESS_END();
}
/*---------------------------------------------------------------------------*/
